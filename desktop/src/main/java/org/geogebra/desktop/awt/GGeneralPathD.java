package org.geogebra.desktop.awt;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.util.debug.Log;

public class GGeneralPathD implements GGeneralPath, GShapeD {

	private GeneralPath impl = new GeneralPath();

	public GGeneralPathD(GeneralPath g) {
		impl = g;
	}

	public GGeneralPathD() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		impl = new GeneralPath(Path2D.WIND_EVEN_ODD);
	}

	public GGeneralPathD(int rule) {
		impl = new GeneralPath(rule);
	}

	public static GeneralPath getAwtGeneralPath(GGeneralPath gp) {
		if (!(gp instanceof GGeneralPathD)) {
			if (gp != null) {
				Log.debug("other type");
			}
			return null;
		}
		return ((GGeneralPathD) gp).impl;
	}

	@Override
	public synchronized void moveTo(double f, double g) {
		impl.moveTo(f, g);

	}

	@Override
	public synchronized void reset() {
		impl.reset();
	}

	@Override
	public synchronized void lineTo(double x, double y) {
		impl.lineTo(x, y);
	}

	@Override
	public synchronized void closePath() {
		impl.closePath();
	}

	@Override
	public synchronized void append(GShape s, boolean connect) {
		if (!(s instanceof GShapeD)) {
			return;
		}
		impl.append(((GShapeD) s).getAwtShape(), connect);

	}

	@Override
	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	@Override
	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	@Override
	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	@Override
	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle(rectangle));
	}

	@Override
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	@Override
	public java.awt.Shape getAwtShape() {
		return impl;
	}

	@Override
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		// TODO Auto-generated method stub
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	@Override
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	@Override
	public GShape createTransformedShape(GAffineTransform affineTransform) {
		return new GGenericShapeD(impl.createTransformedShape(
				((GAffineTransformD) affineTransform).getImpl()));
	}

	@Override
	public GPoint2D getCurrentPoint() {
		if (impl.getCurrentPoint() == null) {
			return null;
		}
		return new GPoint2DD(impl.getCurrentPoint().getX(),
				impl.getCurrentPoint().getY());
	}

	@Override
	public boolean contains(GRectangle2D p) {
		return impl.contains(GGenericRectangle2DD.getAWTRectangle2D(p));
	}

	@Override
	public boolean contains(double arg0, double arg1, double arg2,
			double arg3) {
		return impl.contains(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean contains(GPoint2D p) {
		if (p == null) {
			return false;
		}
		return impl.contains(p.getX(), p.getY());
	}

	@Override
	public void curveTo(double f, double g, double h, double i, double j,
			double k) {
		impl.curveTo(f, g, h, i, j, k);

	}
}
