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

package gecv.struct.convolve;


/**
 * Floating point 1D convolution kernel that extends {@link Kernel1D}.
 *
 * @author Peter Abeles
 */
public class Kernel1D_F32 extends Kernel1D {

	public float data[];

	/**
	 * Creates a new kernel whose initial values are specified by data and width.  The length
	 * of its internal data will be width.  Data must be at least as long as width.
	 *
	 * @param data  The value of the kernel. Not modified.  Reference is not saved.
	 * @param width The kernels width.  Must be odd.
	 */
	public Kernel1D_F32(float data[], int width) {
		if (width % 2 == 0 && width <= 0)
			throw new IllegalArgumentException("invalid width");

		this.width = width;

		this.data = new float[width];
		System.arraycopy(data, 0, this.data, 0, width);
	}

	/**
	 * Create a kernel whose elements are all equal to zero.
	 *
	 * @param width How wide the kernel is.  Must be odd.
	 */
	public Kernel1D_F32(int width) {
		if (width % 2 == 0 && width <= 0)
			throw new IllegalArgumentException("invalid width");
		data = new float[width];
		this.width = width;
	}

	/**
	 * Create a kernel with the specified values.
	 *
	 * @param width How wide the kernel is.  Must be odd.
	 * @param value The kernel
	 */
	public Kernel1D_F32(int width, float... value) {
		if (width % 2 == 0 && width <= 0)
			throw new IllegalArgumentException("invalid width");
		data = new float[width];
		this.width = width;
		System.arraycopy(value, 0, data, 0, width);
	}

	protected Kernel1D_F32() {
	}

	/**
	 * Creates a kernel whose elements are the specified data array and has
	 * the specified width.
	 *
	 * @param data  The array who will be the kernel's data.  Reference is saved.
	 * @param width The kernel's width.
	 * @return A new kernel.
	 */
	public static Kernel1D_F32 wrap(float data[], int width) {
		if (width % 2 == 0 && width <= 0 && width > data.length)
			throw new IllegalArgumentException("invalid width");

		Kernel1D_F32 ret = new Kernel1D_F32();
		ret.data = data;
		ret.width = width;

		return ret;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	public float get(int i) {
		return data[i];
	}

	public float computeSum() {
		float sum = 0;
		for( int i = 0; i < data.length; i++ ) {
			sum += data[i];
		}

		return sum;
	}

	public float[] getData() {
		return data;
	}

	public void print() {
		for (int i = 0; i < width; i++) {
			System.out.printf("%6.3f ", data[i]);
		}
		System.out.println();
	}
}
