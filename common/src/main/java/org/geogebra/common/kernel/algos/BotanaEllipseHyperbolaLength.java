package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

public class BotanaEllipseHyperbolaLength {
	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	public Variable[] getVars() {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoPointND focus1,
			GeoPointND focus2, GeoNumberValue length)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint F1 = (GeoPoint) focus1;
		GeoPoint F2 = (GeoPoint) focus2;

		/* SPECIAL CASE 1: radius is a segment */
		if (length instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
			/*
			 * Here we do the full work for this segment. It would be nicer to
			 * put this code into GeoSegment but we need to use the square of
			 * the length of the segment in this special case.
			 */
			// GeoSegment s = (GeoSegment) this.getInput(1);
			// if (botanaVars == null) {
			// Variable[] centerBotanaVars = P.getBotanaVars(P);
			// botanaVars = new Variable[4];
			// // center P
			// botanaVars[0] = centerBotanaVars[0];
			// botanaVars[1] = centerBotanaVars[1];
			// // point C on the circle
			// botanaVars[2] = new Variable();
			// botanaVars[3] = new Variable();
			// }
			// GeoPoint A = s.getStartPoint();
			// GeoPoint B = s.getEndPoint();
			// Variable[] ABotanaVars = A.getBotanaVars(A);
			// Variable[] BBotanaVars = B.getBotanaVars(B);
			//
			// botanaPolynomials = new Polynomial[2];
			// // C-P == B-A <=> C-P-B+A == 0
			// botanaPolynomials[0] = new Polynomial(botanaVars[2])
			// .subtract(new Polynomial(botanaVars[0]))
			// .subtract(new Polynomial(BBotanaVars[0]))
			// .add(new Polynomial(ABotanaVars[0]));
			// botanaPolynomials[1] = new Polynomial(botanaVars[3])
			// .subtract(new Polynomial(botanaVars[1]))
			// .subtract(new Polynomial(BBotanaVars[1]))
			// .add(new Polynomial(ABotanaVars[1]));
			// // done for both coordinates!
			// return botanaPolynomials;
		}

		/* SPECIAL CASE 2: radius is an expression */

		GeoNumeric num = null;
		if (length instanceof GeoNumeric) {
			num = (GeoNumeric) length;
		}
		if (F1 == null || F2 == null || num == null) {
			throw new NoSymbolicParametersException();
		}

		if (botanaVars == null) {
			Variable[] centerBotanaVars = F1.getBotanaVars(F1);
			Variable[] centerBotanaVars2 = F2.getBotanaVars(F2);
			botanaVars = new Variable[7];
			// center
			botanaVars[0] = centerBotanaVars[0];
			botanaVars[1] = centerBotanaVars[1];

			botanaVars[2] = centerBotanaVars2[0];
			botanaVars[3] = centerBotanaVars2[1];
			// point on circle
			botanaVars[4] = new Variable();
			botanaVars[5] = new Variable();
			// radius
			botanaVars[6] = new Variable();
		}

		botanaPolynomials = new Polynomial[2];
		Polynomial[] extraPolys = null;
		if (num.getParentAlgorithm() instanceof AlgoDependentNumber) {
			extraPolys = num.getBotanaPolynomials(num);
		}
		/*
		 * Note that we read the Botana variables just after reading the Botana
		 * polynomials since the variables are set after the polys are set.
		 */
		Variable[] radiusBotanaVars = num.getBotanaVars(num);
		int k = 0;
		// r^2
		Polynomial sqrR = Polynomial.sqr(new Polynomial(radiusBotanaVars[0]));
		// define radius
		if (extraPolys != null) {
			botanaPolynomials = new Polynomial[extraPolys.length + 1];
			for (k = 0; k < extraPolys.length; k++) {
				botanaPolynomials[k] = extraPolys[k];
			}
		}
		// ((A-(x,y))^2+(B-(x,y))^2-100)^2=4*(B-(x,y))^2*(A-(x,y))^2
		// define circle
		// botanaPolynomials[k] =;
		Polynomial f1distSq = Polynomial.sqrDistance(botanaVars[0],
				botanaVars[1], botanaVars[4], botanaVars[5]);
		Polynomial f2distSq = Polynomial.sqrDistance(botanaVars[2],
				botanaVars[3], botanaVars[4], botanaVars[5]);
		Polynomial lhs = Polynomial.sqr(f1distSq.add(f2distSq).subtract(sqrR));
		Polynomial rhs = f1distSq.multiply(f2distSq)
				.multiply(new Polynomial(4));
		botanaPolynomials[k] = lhs.subtract(rhs);

		return botanaPolynomials;

	}
}