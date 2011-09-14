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

package boofcv.alg.distort.impl;

import boofcv.alg.distort.ImageDistort;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.struct.distort.PixelTransform;
import boofcv.struct.image.ImageUInt8;


/**
 * @author Peter Abeles
 */
public class TestImplImageDistort_I8 extends GeneralImageDistortTests<ImageUInt8>{

	public TestImplImageDistort_I8() {
		super(ImageUInt8.class);
	}

	@Override
	public ImageDistort<ImageUInt8> createDistort(PixelTransform dstToSrc, InterpolatePixel<ImageUInt8> interp) {
		return new ImplImageDistort_I8<ImageUInt8>(dstToSrc,interp);
	}
}