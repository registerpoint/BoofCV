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

package boofcv.abst.filter.derivative;

import boofcv.struct.image.ImageSingleBand;


/**
 * A generic interface for computing image's second derivatives given the image's gradient.  This is typically faster
 * than computing the Hessian directly.
 *
 * @author Peter Abeles
 */
public interface ImageHessian<Output extends ImageSingleBand> extends ImageDerivative<Output,Output> {

	/**
	 * Computes all the second derivative terms in the image.
	 *
	 * @param inputDerivX Precomputed image X-derivative.
	 * @param inputDerivY Precomputed image Y-derivative.
	 * @param derivXX Second derivative x-axis x-axis
	 * @param derivYY Second derivative x-axis y-axis
	 * @param derivXY Second derivative x-axis y-axis
	 */
	public void process( Output inputDerivX , Output inputDerivY, Output derivXX, Output derivYY, Output derivXY  );

}
