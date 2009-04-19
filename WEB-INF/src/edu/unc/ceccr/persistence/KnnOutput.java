package edu.unc.ceccr.persistence;

import javax.persistence.Transient;

public abstract class KnnOutput implements ModelInterface {
	@Transient
	public float getR01_squared() {
		return getR01Squared();
	}

	@Transient
	public float getR02_squared() {
		return getR02Squared();
	}

	@Transient
	public float getQ_squared() {
		return getQSquared();
	}

	@Transient
	public float getR_squared() {
		return getRSquared();
	}

}
