package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * buffers for openGL
 * 
 * @author mathieu
 *
 */
public class GLBufferW implements GLBuffer {

	private MyFloat32Array impl;

	/**
	 * constructor from float array
	 */
	public GLBufferW() {
		isEmpty = true;
		currentLength = 0;
	}

	private boolean isEmpty;

	private int currentLength;

	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public void setEmpty() {
		isEmpty = true;
	}

	@Override
	public void allocate(int length) {
		// allocate buffer only at start and when length change
		if (impl == null || impl.getLength() < length) {
			// This may be null in IE10
			impl = (MyFloat32Array) Float32Array.create(length);
		}

		index = 0;

	}

	private int index = 0;

	@Override
	public void setLimit(int length) {
		currentLength = length;
		isEmpty = false;
	}

	@Override
	public void put(double value) {
		if (impl == null) {
			return;
		}
		impl.set(index, value);
		index++;
	}

	@Override
	public double get() {
		double ret = impl.get(index);
		index++;
		return ret;
	}

	@Override
	public void rewind() {
		index = 0;
	}


	@Override
	public void set(ArrayList<Double> array, int length) {

		allocate(length);
		if (impl == null) {
			return;
		}
		for (int i = 0; i < length; i++) {
			impl.set(i, array.get(i));
		}

		setLimit(length);
	}

	@Override
	public int capacity() {
		return currentLength;
	}

	@Override
	public void array(float[] ret) {
		if (impl == null) {
			return;
		}
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.get(i);
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer() {
		return impl.subarray(0, currentLength);
	}

}
