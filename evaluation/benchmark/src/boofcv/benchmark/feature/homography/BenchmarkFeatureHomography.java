/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.benchmark.feature.homography;

import boofcv.abst.feature.associate.GeneralAssociation;
import boofcv.alg.feature.associate.ScoreAssociateEuclideanSq;
import boofcv.alg.feature.associate.ScoreAssociateTuple;
import boofcv.factory.feature.associate.FactoryAssociationTuple;
import boofcv.struct.FastQueue;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.TupleDesc_F64;
import georegression.geometry.UtilPoint2D_F32;
import georegression.struct.homo.Homography2D_F32;
import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Point2D_I32;
import georegression.transform.homo.HomographyPointOps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Benchmarks algorithms against a sequence of real images where the homography between the images
 * is known.
 *
 * @author Peter Abeles
 */
public class BenchmarkFeatureHomography {
	GeneralAssociation<TupleDesc_F64> assoc;
	List<Homography2D_F32> transforms;
	String directory;
	double tolerance;

	List<String> nameBase = new ArrayList<String>();

	int numMatches;
	double fractionCorrect;

	public BenchmarkFeatureHomography(GeneralAssociation<TupleDesc_F64> assoc,
									  String directory,
									  String imageSuffix ,
									  double tolerance) {
		this.assoc = assoc;
		this.directory = directory;
		this.tolerance = tolerance;

		nameBase = loadNameBase( directory , imageSuffix );

		transforms = new ArrayList<Homography2D_F32>();
		for( int i=1; i < nameBase.size(); i++ ) {
			String fileName = "H1to"+(i+1)+"p";
			transforms.add( LoadBenchmarkFiles.loadHomography(directory+"/"+fileName));
		}
	}

	private List<String> loadNameBase(String directory, String imageSuffix) {
		List<String> ret = new ArrayList<String>();
		File dir = new File(directory);

		for( File f : dir.listFiles() ) {
			if( !(f.isFile() && f.getName().endsWith(imageSuffix))) {
				continue;
			}

			String name = f.getPath();
			ret.add( name.substring(0,name.length()-imageSuffix.length()));
		}

		// put the names into order
		Collections.sort(ret);

		return ret;
	}

	public void evaluate( String algSuffix ) {
		System.out.println("\n"+algSuffix);

		// load descriptions in the keyframe
		List<FeatureInfo> keyFrame = LoadBenchmarkFiles.loadDescription(nameBase.get(0)+algSuffix);

		for( int i = 1; i < nameBase.size(); i++ ) {
			System.out.println("Examining image "+i);
			List<FeatureInfo> targetFrame = LoadBenchmarkFiles.loadDescription(nameBase.get(i)+algSuffix);

			Homography2D_F32 keyToTarget = transforms.get(i-1);

			associationScore(keyFrame,targetFrame,keyToTarget);
			System.out.println(i+" "+numMatches+" "+fractionCorrect);
		}
	}

	private void associationScore(List<FeatureInfo> keyFrame,
								  List<FeatureInfo> targetFrame,
								  Homography2D_F32 keyToTarget) {

		FastQueue<TupleDesc_F64> listSrc = new FastQueue<TupleDesc_F64>(keyFrame.size(),TupleDesc_F64.class,false);
		FastQueue<TupleDesc_F64> listDst = new FastQueue<TupleDesc_F64>(keyFrame.size(),TupleDesc_F64.class,false);

		for( FeatureInfo f : keyFrame ) {
			listSrc.add(f.getDescription());
		}

		for( FeatureInfo f : targetFrame ) {
			listDst.add(f.getDescription());
		}

		assoc.associate(listSrc,listDst);

		FastQueue<AssociatedIndex> matches = assoc.getMatches();

		Point2D_F32 src = new Point2D_F32();
		Point2D_F32 expected = new Point2D_F32();

		int numCorrect = 0;

		for( int i = 0; i < matches.size; i++ ) {
			AssociatedIndex a = matches.get(i);
			Point2D_I32 s = keyFrame.get(a.src).getLocation();
			Point2D_I32 d = targetFrame.get(a.dst).getLocation();

			src.set(s.x,s.y);

			HomographyPointOps.transform(keyToTarget,src,expected);

			double dist = UtilPoint2D_F32.distance(expected.x,expected.y,d.x,d.y);

			if( dist <= tolerance ) {
				numCorrect++;
			}
		}

		numMatches = matches.size();
		fractionCorrect = ((double)numCorrect)/((double)numMatches);
	}

	public static void main( String args[] ) {
		double tolerance = 3;

		ScoreAssociateTuple score = new ScoreAssociateEuclideanSq();
		GeneralAssociation<TupleDesc_F64> assoc = FactoryAssociationTuple.forwardBackwards(score,-1);

		BenchmarkFeatureHomography app = new BenchmarkFeatureHomography(assoc,"evaluation/data/mikolajczk/ubc/",".png",tolerance);

		app.evaluate("_BoofCV_SURF.txt");
		app.evaluate("_NEW.txt");
	}
}