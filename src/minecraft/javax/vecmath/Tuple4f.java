package javax.vecmath;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class Tuple4f implements java.io.Serializable, Cloneable {

	static final long serialVersionUID = 7068460319248845763L;
	
	public float x;
	public float y;
	public float z;
	public float w;

	public Tuple4f(float[] t) {
		this.x = t[0];
		this.y = t[1];
		this.z = t[2];
		this.w = t[3];
	}

	public Tuple4f(Tuple4f t1) {
		this.x = t1.x;
		this.y = t1.y;
		this.z = t1.z;
		this.w = t1.w;
	}

	public Tuple4f(Tuple4d t1) {
		this.x = (float) t1.x;
		this.y = (float) t1.y;
		this.z = (float) t1.z;
		this.w = (float) t1.w;
	}

	public Tuple4f() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
		this.w = 0.0f;
	}

	public final void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public final void set(float[] t) {
		this.x = t[0];
		this.y = t[1];
		this.z = t[2];
		this.w = t[3];
	}

	public final void set(Tuple4f t1) {
		this.x = t1.x;
		this.y = t1.y;
		this.z = t1.z;
		this.w = t1.w;
	}

	public final void set(Tuple4d t1) {
		this.x = (float) t1.x;
		this.y = (float) t1.y;
		this.z = (float) t1.z;
		this.w = (float) t1.w;
	}

	public final void get(float[] t) {
		t[0] = this.x;
		t[1] = this.y;
		t[2] = this.z;
		t[3] = this.w;
	}

	public final void get(Tuple4f t) {
		t.x = this.x;
		t.y = this.y;
		t.z = this.z;
		t.w = this.w;
	}

	public final void add(Tuple4f t1, Tuple4f t2) {
		this.x = t1.x + t2.x;
		this.y = t1.y + t2.y;
		this.z = t1.z + t2.z;
		this.w = t1.w + t2.w;
	}

	/**
	 * Sets the value of this tuple to the sum of itself and t1.
	 * 
	 * @param t1 the other tuple
	 */
	public final void add(Tuple4f t1) {
		this.x += t1.x;
		this.y += t1.y;
		this.z += t1.z;
		this.w += t1.w;
	}

	/**
	 * Sets the value of this tuple to the difference of tuples t1 and t2 (this = t1
	 * - t2).
	 * 
	 * @param t1 the first tuple
	 * @param t2 the second tuple
	 */
	public final void sub(Tuple4f t1, Tuple4f t2) {
		this.x = t1.x - t2.x;
		this.y = t1.y - t2.y;
		this.z = t1.z - t2.z;
		this.w = t1.w - t2.w;
	}

	/**
	 * Sets the value of this tuple to the difference of itself and t1 (this = this
	 * - t1).
	 * 
	 * @param t1 the other tuple
	 */
	public final void sub(Tuple4f t1) {
		this.x -= t1.x;
		this.y -= t1.y;
		this.z -= t1.z;
		this.w -= t1.w;
	}

	/**
	 * Sets the value of this tuple to the negation of tuple t1.
	 * 
	 * @param t1 the source tuple
	 */
	public final void negate(Tuple4f t1) {
		this.x = -t1.x;
		this.y = -t1.y;
		this.z = -t1.z;
		this.w = -t1.w;
	}

	/**
	 * Negates the value of this tuple in place.
	 */
	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	/**
	 * Sets the value of this tuple to the scalar multiplication of tuple t1.
	 * 
	 * @param s  the scalar value
	 * @param t1 the source tuple
	 */
	public final void scale(float s, Tuple4f t1) {
		this.x = s * t1.x;
		this.y = s * t1.y;
		this.z = s * t1.z;
		this.w = s * t1.w;
	}

	/**
	 * Sets the value of this tuple to the scalar multiplication of the scale factor
	 * with this.
	 * 
	 * @param s the scalar value
	 */
	public final void scale(float s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		this.w *= s;
	}

	/**
	 * Sets the value of this tuple to the scalar multiplication of tuple t1 plus
	 * tuple t2 (this = s*t1 + t2).
	 * 
	 * @param s  the scalar value
	 * @param t1 the tuple to be multipled
	 * @param t2 the tuple to be added
	 */
	public final void scaleAdd(float s, Tuple4f t1, Tuple4f t2) {
		this.x = s * t1.x + t2.x;
		this.y = s * t1.y + t2.y;
		this.z = s * t1.z + t2.z;
		this.w = s * t1.w + t2.w;
	}

	/**
	 * Sets the value of this tuple to the scalar multiplication of itself and then
	 * adds tuple t1 (this = s*this + t1).
	 * 
	 * @param s  the scalar value
	 * @param t1 the tuple to be added
	 */
	public final void scaleAdd(float s, Tuple4f t1) {
		this.x = s * this.x + t1.x;
		this.y = s * this.y + t1.y;
		this.z = s * this.z + t1.z;
		this.w = s * this.w + t1.w;
	}

	/**
	 * Returns a string that contains the values of this Tuple4f. The form is
	 * (x,y,z,w).
	 * 
	 * @return the String representation
	 */
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	/**
	 * Returns true if all of the data members of Tuple4f t1 are equal to the
	 * corresponding data members in this Tuple4f.
	 * 
	 * @param t1 the vector with which the comparison is made
	 * @return true or false
	 */
	public boolean equals(Tuple4f t1) {
		try {
			return (this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w);
		} catch (NullPointerException e2) {
			return false;
		}
	}

	/**
	 * Returns true if the Object t1 is of type Tuple4f and all of the data members
	 * of t1 are equal to the corresponding data members in this Tuple4f.
	 * 
	 * @param t1 the object with which the comparison is made
	 * @return true or false
	 */
	@Override
	public boolean equals(Object t1) {
		try {
			Tuple4f t2 = (Tuple4f) t1;
			return (this.x == t2.x && this.y == t2.y && this.z == t2.z && this.w == t2.w);
		} catch (NullPointerException e2) {
			return false;
		} catch (ClassCastException e1) {
			return false;
		}
	}

	/**
	 * Returns true if the L-infinite distance between this tuple and tuple t1 is
	 * less than or equal to the epsilon parameter, otherwise returns false. The
	 * L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2), abs(z1-z2),
	 * abs(w1-w2)].
	 * 
	 * @param t1      the tuple to be compared to this tuple
	 * @param epsilon the threshold value
	 * @return true or false
	 */
	public boolean epsilonEquals(Tuple4f t1, float epsilon) {
		float diff;

		diff = x - t1.x;
		if (Float.isNaN(diff))
			return false;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = y - t1.y;
		if (Float.isNaN(diff))
			return false;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = z - t1.z;
		if (Float.isNaN(diff))
			return false;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		diff = w - t1.w;
		if (Float.isNaN(diff))
			return false;
		if ((diff < 0 ? -diff : diff) > epsilon)
			return false;

		return true;
	}

	/**
	 * Returns a hash code value based on the data values in this object. Two
	 * different Tuple4f objects with identical data values (i.e., Tuple4f.equals
	 * returns true) will return the same hash code value. Two objects with
	 * different data members may return the same hash value, although this is not
	 * likely.
	 * 
	 * @return the integer hash code value
	 */
	@Override
	public int hashCode() {
		long bits = 1L;
		bits = VecMathUtil.hashFloatBits(bits, x);
		bits = VecMathUtil.hashFloatBits(bits, y);
		bits = VecMathUtil.hashFloatBits(bits, z);
		bits = VecMathUtil.hashFloatBits(bits, w);
		return VecMathUtil.hashFinish(bits);
	}

	/**
	 * Clamps the tuple parameter to the range [low, high] and places the values
	 * into this tuple.
	 * 
	 * @param min the lowest value in the tuple after clamping
	 * @param max the highest value in the tuple after clamping
	 * @param t   the source tuple, which will not be modified
	 */
	public final void clamp(float min, float max, Tuple4f t) {
		if (t.x > max) {
			x = max;
		} else if (t.x < min) {
			x = min;
		} else {
			x = t.x;
		}

		if (t.y > max) {
			y = max;
		} else if (t.y < min) {
			y = min;
		} else {
			y = t.y;
		}

		if (t.z > max) {
			z = max;
		} else if (t.z < min) {
			z = min;
		} else {
			z = t.z;
		}

		if (t.w > max) {
			w = max;
		} else if (t.w < min) {
			w = min;
		} else {
			w = t.w;
		}

	}

	/**
	 * Clamps the minimum value of the tuple parameter to the min parameter and
	 * places the values into this tuple.
	 * 
	 * @param min the lowest value in the tuple after clamping
	 * @param t   the source tuple, which will not be modified
	 */
	public final void clampMin(float min, Tuple4f t) {
		if (t.x < min) {
			x = min;
		} else {
			x = t.x;
		}

		if (t.y < min) {
			y = min;
		} else {
			y = t.y;
		}

		if (t.z < min) {
			z = min;
		} else {
			z = t.z;
		}

		if (t.w < min) {
			w = min;
		} else {
			w = t.w;
		}

	}

	/**
	 * Clamps the maximum value of the tuple parameter to the max parameter and
	 * places the values into this tuple.
	 * 
	 * @param max the highest value in the tuple after clamping
	 * @param t   the source tuple, which will not be modified
	 */
	public final void clampMax(float max, Tuple4f t) {
		if (t.x > max) {
			x = max;
		} else {
			x = t.x;
		}

		if (t.y > max) {
			y = max;
		} else {
			y = t.y;
		}

		if (t.z > max) {
			z = max;
		} else {
			z = t.z;
		}

		if (t.w > max) {
			w = max;
		} else {
			w = t.z;
		}

	}

	/**
	 * Sets each component of the tuple parameter to its absolute value and places
	 * the modified values into this tuple.
	 * 
	 * @param t the source tuple, which will not be modified
	 */
	public final void absolute(Tuple4f t) {
		x = Math.abs(t.x);
		y = Math.abs(t.y);
		z = Math.abs(t.z);
		w = Math.abs(t.w);
	}

	/**
	 * Clamps this tuple to the range [low, high].
	 * 
	 * @param min the lowest value in this tuple after clamping
	 * @param max the highest value in this tuple after clamping
	 */
	public final void clamp(float min, float max) {
		if (x > max) {
			x = max;
		} else if (x < min) {
			x = min;
		}

		if (y > max) {
			y = max;
		} else if (y < min) {
			y = min;
		}

		if (z > max) {
			z = max;
		} else if (z < min) {
			z = min;
		}

		if (w > max) {
			w = max;
		} else if (w < min) {
			w = min;
		}

	}

	/**
	 * Clamps the minimum value of this tuple to the min parameter.
	 * 
	 * @param min the lowest value in this tuple after clamping
	 */
	public final void clampMin(float min) {
		if (x < min)
			x = min;
		if (y < min)
			y = min;
		if (z < min)
			z = min;
		if (w < min)
			w = min;

	}

	public final void clampMax(float max) {
		if (x > max)
			x = max;
		if (y > max)
			y = max;
		if (z > max)
			z = max;
		if (w > max)
			w = max;

	}

	public final void absolute() {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		w = Math.abs(w);
	}

	public void interpolate(Tuple4f t1, Tuple4f t2, float alpha) {
		this.x = (1 - alpha) * t1.x + alpha * t2.x;
		this.y = (1 - alpha) * t1.y + alpha * t2.y;
		this.z = (1 - alpha) * t1.z + alpha * t2.z;
		this.w = (1 - alpha) * t1.w + alpha * t2.w;

	}

	public void interpolate(Tuple4f t1, float alpha) {
		this.x = (1 - alpha) * this.x + alpha * t1.x;
		this.y = (1 - alpha) * this.y + alpha * t1.y;
		this.z = (1 - alpha) * this.z + alpha * t1.z;
		this.w = (1 - alpha) * this.w + alpha * t1.w;

	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

}
