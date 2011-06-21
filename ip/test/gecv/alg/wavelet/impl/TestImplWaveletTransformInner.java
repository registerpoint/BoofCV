/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.wavelet.impl;

import gecv.alg.wavelet.UtilWavelet;
import gecv.struct.image.ImageFloat32;
import gecv.struct.wavelet.WaveletDesc_F32;
import gecv.testing.GecvTesting;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
// todo test sub-image
public class TestImplWaveletTransformInner {

	@Test
	public void horizontal() {
		PermuteWaveletCompareToNaive test = new PermuteWaveletCompareToNaive() {

			@Override
			public void applyValidation(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformNaive.horizontal(desc,input,output);
			}

			@Override
			public void applyTransform(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformInner.horizontal(desc,input,output);
			}

			@Override
			public void compareResults(WaveletDesc_F32 desc, ImageFloat32 expected, ImageFloat32 found, boolean shrunk) {
				equalsTranHorizontal(desc,expected,found,shrunk);
			}
		};

		test.runTests(true);
	}

	@Test
	public void vertical() {
		PermuteWaveletCompareToNaive test = new PermuteWaveletCompareToNaive() {

			@Override
			public void applyValidation(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformNaive.vertical(desc,input,output);
			}

			@Override
			public void applyTransform(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformInner.vertical(desc,input,output);
			}

			@Override
			public void compareResults(WaveletDesc_F32 desc, ImageFloat32 expected, ImageFloat32 found, boolean shrunk) {
				equalsTranVertical(desc,expected,found,shrunk);
			}
		};

		test.runTests(true);
	}

	@Test
	public void horizontalInverse() {
		PermuteWaveletCompareToNaive test = new PermuteWaveletCompareToNaive() {

			@Override
			public void applyValidation(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformNaive.horizontalInverse(desc,input,output);
			}

			@Override
			public void applyTransform(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformInner.horizontalInverse(desc,input,output);
			}

			@Override
			public void compareResults(WaveletDesc_F32 desc,
									   ImageFloat32 expected, ImageFloat32 found,
									   boolean shrunk) {
				int shrink = shrunk ? 1 : 0;
				int border = Math.max(UtilWavelet.computeBorderStart(desc),
						UtilWavelet.computeBorderEnd(desc,width-shrink))-desc.offsetScaling*2;

				GecvTesting.assertEquals(expected,found,border,1e-4f);
			}
		};

		test.runTests(false);
	}

	@Test
	public void verticalInverse() {
		PermuteWaveletCompareToNaive test = new PermuteWaveletCompareToNaive() {

			@Override
			public void applyValidation(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformNaive.verticalInverse(desc,input,output);
			}

			@Override
			public void applyTransform(WaveletDesc_F32 desc, ImageFloat32 input, ImageFloat32 output) {
				ImplWaveletTransformInner.verticalInverse(desc,input,output);
			}

			@Override
			public void compareResults(WaveletDesc_F32 desc,
									   ImageFloat32 expected, ImageFloat32 found,
									   boolean shrunk) {
				int shrink = shrunk ? 1 : 0;
				int border = Math.max(UtilWavelet.computeBorderStart(desc),
						UtilWavelet.computeBorderEnd(desc,height-shrink))-desc.offsetScaling*2;

				GecvTesting.assertEquals(expected,found,border,1e-4f);
			}
		};

		test.runTests(false);
	}

	private void equalsTranHorizontal( WaveletDesc_F32 desc,
								   ImageFloat32 expected , ImageFloat32 found , boolean isOdd ) {
		int minus = isOdd ? -1 : 0;
		int begin = UtilWavelet.computeBorderStart(desc);
		int end = expected.getWidth()-UtilWavelet.computeBorderEnd(desc,expected.width+minus);

		int w = expected.width;
		int h = expected.height;

		equalsTranHorizontal(expected.subimage(0,0,w/2,h),found.subimage(0,0,w/2,h),begin/2,end/2,"left");
		equalsTranHorizontal(expected.subimage(w/2,0,w,h),found.subimage(w/2,0,w,h),begin/2,end/2,"right");
	}

	private void equalsTranHorizontal( ImageFloat32 expected , ImageFloat32 found ,
								   int begin , int end , String quad ) {

		for( int y = 0; y < expected.height; y++ ) {
			for( int x = 0; x < expected.width; x++ ) {
				// see if the inner image is identical to the naive implementation
				// the border should be unmodified, zeros
				if( x >= begin && x < end )
					assertEquals(quad+" ( "+x+" , "+y+" )",expected.get(x,y) , found.get(x,y) , 1e-4f);
				else
					assertTrue(quad+" ( "+x+" , "+y+" ) 0 != "+found.get(x,y),0 == found.get(x,y));
			}
		}
	}

	private void equalsTranVertical( WaveletDesc_F32 desc,
								   ImageFloat32 expected , ImageFloat32 found , boolean isOdd ) {
		int minus = isOdd ? -1 : 0;
		int begin = UtilWavelet.computeBorderStart(desc);
		int end = expected.getHeight()-UtilWavelet.computeBorderEnd(desc,expected.height+minus);

		int w = expected.width;
		int h = expected.height;

		equalsTranVertical(expected.subimage(0,0,w,h/2),found.subimage(0,0,w,h/2),begin/2,end/2,"top");
		equalsTranVertical(expected.subimage(0,h/2,w,h),found.subimage(0,h/2,w,h),begin/2,end/2,"bottom");
	}

	private void equalsTranVertical( ImageFloat32 expected , ImageFloat32 found ,
								   int begin , int end , String quad ) {

		for( int y = 0; y < expected.height; y++ ) {
			// see if the inner image is identical to the naive implementation
			// the border should be unmodified, zeros
			if( y >= begin && y < end ) {
				for( int x = 0; x < expected.width; x++ ) {
					assertEquals(quad+" ( "+x+" , "+y+" )",expected.get(x,y) , found.get(x,y) , 1e-4f);
				}
			} else {
				for( int x = 0; x < expected.width; x++ ) {
					assertTrue(quad+" ( "+x+" , "+y+" ) 0 != "+found.get(x,y),0 == found.get(x,y));
				}
			}
		}
	}

}
