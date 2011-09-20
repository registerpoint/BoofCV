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

package boofcv.factory.filter.derivative;

import boofcv.abst.filter.FilterImageInterface;
import boofcv.abst.filter.derivative.*;
import boofcv.alg.filter.derivative.*;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.core.image.border.BorderType;
import boofcv.core.image.border.ImageBorder_F32;
import boofcv.core.image.border.ImageBorder_I32;
import boofcv.factory.filter.convolve.FactoryConvolve;
import boofcv.factory.filter.kernel.FactoryKernelGaussian;
import boofcv.struct.convolve.Kernel1D;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

import java.lang.reflect.Method;

/**
 * <p>
 * Factory for creating different types of {@link boofcv.abst.filter.derivative.ImageGradient}, which are used to compute
 * the image's derivative.
 * </p>
 *
 * <p>
 * If the image borders are processed then how the borders are used needs to be selected carefully.  Default
 * values are selected to maximize visual appearance, which means sacrificing some theoretical purity.
 * <ul>
 * <li>No border: The border is simply ignored.  This can be problematic when dealing with small images in an image pyramid.</li>
 * <li>Extended: Arguably introduces the fewest visible artifacts at the border by replicating the border pixels.
 * Has the undesirable affect of breaking the associative property, e.g. second derivatives XY and YX are not equal.<li>
 * <li>Wrap: Maintains the associative property but can introduce strange artifacts because the pixel values at the other end
 * of the image are only loosely correlated.<li>
 * <li>Reflect: Doesn't have any justification for its use in this application.<li>
 * <li>Normalized: Will generate complete garbage<li>
 * <li>Value: Is sometimes used and will generate significant artifacts along the border.<li>
 * </ul>
 * </p>
 *
 * @author Peter Abeles
 */
public class FactoryDerivative {

	public static <I extends ImageBase, D extends ImageBase>
	ImageGradient<I,D> prewitt( Class<I> inputType , Class<D> derivType)
	{
		Method m = findDerivative(GradientPrewitt.class,inputType,derivType);
		return new ImageGradient_Reflection<I,D>(m);
	}

	public static <I extends ImageBase, D extends ImageBase>
	ImageGradient<I,D> sobel( Class<I> inputType , Class<D> derivType)
	{
		Method m = findDerivative(GradientSobel.class,inputType,derivType);
		return new ImageGradient_Reflection<I,D>(m);
	}

	public static <I extends ImageBase, D extends ImageBase>
	ImageGradient<I,D> three( Class<I> inputType , Class<D> derivType)
	{
		Method m = findDerivative(GradientThree.class,inputType,derivType);
		return new ImageGradient_Reflection<I,D>(m);
	}

	public static <I extends ImageBase, D extends ImageBase>
	ImageHessianDirect<I,D> hessianDirectThree( Class<I> inputType , Class<D> derivType)
	{
		Method m = findHessian(HessianThree.class,inputType,derivType);
		return new ImageHessianDirect_Reflection<I,D>(m);
	}

	public static <I extends ImageBase, D extends ImageBase>
	ImageHessianDirect<I,D> hessianDirectSobel( Class<I> inputType , Class<D> derivType)
	{
		Method m = findHessian(HessianSobel.class,inputType,derivType);
		return new ImageHessianDirect_Reflection<I,D>(m);
	}

	public static <D extends ImageBase>
	ImageHessian<D> hessian( Class<?> gradientType , Class<D> derivType ) {
		Method m = findHessianFromGradient(gradientType,derivType);
		return new ImageHessian_Reflection<D>(m);
	}

	public static <I extends ImageBase, D extends ImageBase>
	ImageGradient<I,D> gaussian( double sigma , int radius , Class<I> inputType , Class<D> derivType) {
		return new ImageGradient_Gaussian<I,D>(sigma,radius,inputType,derivType);
	}

	public static ImageGradient<ImageFloat32,ImageFloat32> gaussian_F32( double sigma , int radius ) {
		return gaussian(sigma,radius, ImageFloat32.class,ImageFloat32.class);
	}

	public static ImageGradient<ImageFloat32,ImageFloat32> sobel_F32() {
		return sobel(ImageFloat32.class,ImageFloat32.class);
	}

	public static ImageGradient<ImageFloat32,ImageFloat32> three_F32() {
		return three(ImageFloat32.class,ImageFloat32.class);
	}

	public static ImageHessianDirect<ImageFloat32,ImageFloat32> hessianDirectThree_F32() {
		return hessianDirectThree(ImageFloat32.class,ImageFloat32.class);
	}

	public static ImageHessianDirect<ImageFloat32,ImageFloat32> hessianDirectSobel_F32() {
		return hessianDirectSobel(ImageFloat32.class,ImageFloat32.class);
	}

	public static ImageGradient<ImageUInt8, ImageSInt16> gaussian_I8( double sigma , int radius ) {
		return gaussian(sigma,radius,ImageUInt8.class,ImageSInt16.class);
	}

	public static ImageGradient<ImageUInt8, ImageSInt16> sobel_I8() {
		return sobel(ImageUInt8.class,ImageSInt16.class);
	}

	public static ImageGradient<ImageUInt8, ImageSInt16> three_I8() {
		return three(ImageUInt8.class,ImageSInt16.class);
	}

	public static ImageHessianDirect<ImageUInt8, ImageSInt16> hessianDirectThree_I8() {
		return hessianDirectThree(ImageUInt8.class,ImageSInt16.class);
	}

	public static ImageHessianDirect<ImageUInt8, ImageSInt16> hessianDirectSobel_I8() {
		return hessianDirectSobel(ImageUInt8.class,ImageSInt16.class);
	}

	public static <D extends ImageBase> ImageHessian<D> hessianSobel( Class<D> derivType ) {
		if( derivType == ImageFloat32.class )
			return (ImageHessian<D>)hessian(GradientSobel.class,ImageFloat32.class);
		else if( derivType == ImageSInt16.class )
			return (ImageHessian<D>)hessian(GradientSobel.class,ImageSInt16.class);
		else
			throw new IllegalArgumentException("Not supported yet");
	}

	public static <D extends ImageBase> ImageHessian<D> hessianPrewitt( Class<D> derivType ) {
		if( derivType == ImageFloat32.class )
			return (ImageHessian<D>)hessian(GradientPrewitt.class,ImageFloat32.class);
		else if( derivType == ImageSInt16.class )
			return (ImageHessian<D>)hessian(GradientPrewitt.class,ImageSInt16.class);
		else
			throw new IllegalArgumentException("Not supported yet");
	}

	public static <D extends ImageBase> ImageHessian<D> hessianThree( Class<D> derivType ) {
		if( derivType == ImageFloat32.class )
			return (ImageHessian<D>)hessian(GradientThree.class,ImageFloat32.class);
		else if( derivType == ImageSInt16.class )
			return (ImageHessian<D>)hessian(GradientThree.class,ImageSInt16.class);
		else
			throw new IllegalArgumentException("Not supported yet");
	}

	public static <D extends ImageBase> ImageHessian<D> hessianGaussian( int radius , double sigma ,
																		 Class<D> derivType )
	{
		// need to do this here to make sure the blur and derivative functions have the same paramters.
		if( radius <= 0 )
			radius = FactoryKernelGaussian.radiusForSigma(sigma,1);
		else if( sigma <= 0 )
			sigma = FactoryKernelGaussian.sigmaForRadius(radius,1);

		Kernel1D kernelDeriv = FactoryKernelGaussian.derivativeI(derivType,1,sigma,radius);

		BorderType borderDeriv = BorderType.EXTENDED;
		FilterImageInterface<D, D> derivX = FactoryConvolve.convolve(kernelDeriv,derivType,derivType, borderDeriv,true);
		FilterImageInterface<D, D> derivY = FactoryConvolve.convolve(kernelDeriv,derivType,derivType, borderDeriv,false);

		return new ImageHessian_Filter<D>(derivX,derivY,derivType);
	}

	private static Method findDerivative(Class<?> derivativeClass,
										 Class<?> inputType , Class<?> derivType ) {
		Method m;
		try {
			Class<?> borderType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageBorder_F32.class : ImageBorder_I32.class;
			m = derivativeClass.getDeclaredMethod("process", inputType,derivType,derivType,borderType);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return m;
	}

	private static Method findHessian(Class<?> derivativeClass,
										Class<?> inputType , Class<?> derivType ) {
		Method m;
		try {
			Class<?> borderType = GeneralizedImageOps.isFloatingPoint(inputType) ? ImageBorder_F32.class : ImageBorder_I32.class;
			m = derivativeClass.getDeclaredMethod("process", inputType,derivType,derivType,derivType,borderType);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return m;
	}

	private static Method findHessianFromGradient(Class<?> derivativeClass, Class<?> imageType ) {
		String name = derivativeClass.getSimpleName().substring(8);
		Method m;
		try {
			Class<?> borderType = GeneralizedImageOps.isFloatingPoint(imageType) ? ImageBorder_F32.class : ImageBorder_I32.class;
			m = HessianFromGradient.class.getDeclaredMethod("hessian"+name, imageType,imageType,imageType,imageType,imageType,borderType);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return m;
	}
}
