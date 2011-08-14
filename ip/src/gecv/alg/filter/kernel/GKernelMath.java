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

package gecv.alg.filter.kernel;

import gecv.struct.convolve.*;
import gecv.struct.image.ImageBase;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageInteger;


/**
 * Contains generalized function with weak typing from {@link KernelMath}.
 *
 * @author Peter Abeles
 */
public class GKernelMath {
	
	public static Kernel2D transpose( Kernel2D a ) {
		if( a instanceof Kernel2D_F32)
			return KernelMath.transpose((Kernel2D_F32)a);
		else
			return KernelMath.transpose((Kernel2D_I32)a);
	}

	public static Kernel2D convolve( Kernel1D a , Kernel1D b ) {
		if( a.isInteger() != b.isInteger() )
			throw new IllegalArgumentException("But input kernels must be of the same type.");

		if( a.isInteger() ) {
			return KernelMath.convolve((Kernel1D_I32)a,(Kernel1D_I32)b);
		} else {
			return KernelMath.convolve((Kernel1D_F32)a,(Kernel1D_F32)b);
		}
	}

	public static <T extends ImageBase> T convertToImage( Kernel2D kernel ) {
		if( kernel.isInteger() ) {
			return (T)KernelMath.convertToImage((Kernel2D_I32)kernel);
		} else {
			return (T)KernelMath.convertToImage((Kernel2D_F32)kernel);
		}
	}

	public static Kernel2D convertToKernel( ImageBase image ) {
		if( image.getTypeInfo().isInteger() ) {
			return KernelMath.convertToKernel((ImageInteger)image);
		} else {
			return KernelMath.convertToKernel((ImageFloat32)image);
		}
	}
}
