/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
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

package boofcv.app;

import boofcv.alg.feature.detect.InvalidCalibrationTarget;
import boofcv.alg.feature.detect.grid.*;
import boofcv.alg.feature.detect.quadblob.QuadBlob;
import boofcv.alg.geo.calibration.CalibrationGridConfig;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.point.Point2D_F64;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class WrapPlanarGridTarget implements CalibrationGridInterface{

	int squareColumns;

	int pointColumns;
	int pointRows;

	RefineCalibrationGridCorner refine;
	AutoThresholdCalibrationGrid autoThreshold;
	DetectSpacedSquareGrid detect;

	// set of found points
	List<Point2D_F64> ret;

	public WrapPlanarGridTarget() {
		refine = new WrapRefineCornerSegmentFit();
	}

	@Override
	public void configure(CalibrationGridConfig config) {
		
		squareColumns = config.gridWidth/2;
		int squareRows = config.gridHeight/2;

		pointColumns = squareColumns*2;
		pointRows = squareRows*2;

		detect = new DetectSpacedSquareGrid(500, squareColumns,squareRows);
		autoThreshold = new AutoThresholdCalibrationGrid(255,30);

	}

	@Override
	public boolean process(ImageFloat32 input) {

		// detect the target at pixel level accuracy
		if( !autoThreshold.process(detect,input) )
			return false;

		List<QuadBlob> squares = detect.getSquaresOrdered();
		
		// refine the corner accuracy estimate to sub-pixel
		try {
			refine.refine(squares,input);
		} catch( InvalidCalibrationTarget e ) {
			e.printStackTrace();
			return false;
		}
			
		ret = new ArrayList<Point2D_F64>();
		UtilCalibrationGrid.extractOrderedSubpixel(squares,ret, squareColumns);
		UtilCalibrationGrid.enforceClockwiseOrder(ret,pointRows,pointColumns);

		return true;
	}

	@Override
	public List<Point2D_F64> getPoints() {
		return ret;
	}
}
