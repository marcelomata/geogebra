package com.himamis.retex.editor.share.model;

import java.util.HashMap;

/**
 * @author Michael Borcherds
 * 
 *         Various methods for converting between Korean Unicode formats
 * 
 *         http://gernot-katzers-spice-pages.com/var/korean_hangul_unicode.html
 * 
 *         https://en.wikipedia.org/wiki/Hangul_Compatibility_Jamo
 * 
 *         https://en.wikipedia.org/wiki/Hangul_Syllables
 * 
 *         https://en.wikipedia.org/wiki/Korean_language_and_computers#Example
 * 
 *         https://en.wikipedia.org/wiki/Hangul_Jamo_(Unicode_block)
 * 
 *
 */
public class Korean {

	static StringBuilder sb;
	static HashMap<Character, Character> koreanLeadToTail = null;
	static HashMap<Character, Character> koreanTailToLead = null;

	static HashMap<Character, Character> getKoreanLeadToTail() {

		if (koreanLeadToTail == null) {
			koreanLeadToTail = new HashMap<Character, Character>();

			koreanLeadToTail.put('\u1100', '\u11a8');
			koreanLeadToTail.put('\u1101', '\u11a9');
			koreanLeadToTail.put('\u1102', '\u11ab');
			koreanLeadToTail.put('\u1103', '\u11ae');

			// map to itself
			koreanLeadToTail.put('\u1104', '\u1104');

			koreanLeadToTail.put('\u1105', '\u11af');
			koreanLeadToTail.put('\u1106', '\u11b7');
			koreanLeadToTail.put('\u1107', '\u11b8');

			// map to itself
			koreanLeadToTail.put('\u1108', '\u1108');

			koreanLeadToTail.put('\u1109', '\u11ba');
			koreanLeadToTail.put('\u110a', '\u11bb');
			koreanLeadToTail.put('\u110b', '\u11bc');
			koreanLeadToTail.put('\u110c', '\u11bd');

			// map to itself
			koreanLeadToTail.put('\u110d', '\u110d');

			koreanLeadToTail.put('\u110e', '\u11be');
			koreanLeadToTail.put('\u110f', '\u11bf');
			koreanLeadToTail.put('\u1110', '\u11c0');
			koreanLeadToTail.put('\u1111', '\u11c1');
			koreanLeadToTail.put('\u1112', '\u11c2');
		}

		return koreanLeadToTail;

	}

	public static Character tailToLead(char ch) {
		
		return getKoreanTailToLead().get(ch);
	}

	static HashMap<Character, Character> getKoreanTailToLead() {

		if (koreanTailToLead == null) {
			koreanTailToLead = new HashMap<Character, Character>();

			koreanTailToLead.put('\u11a8', '\u1100');
			koreanTailToLead.put('\u11a9', '\u1101');
			koreanTailToLead.put('\u11ab', '\u1102');
			koreanTailToLead.put('\u11ae', '\u1103');

			// map to itself
			// koreanTailToLead.put('\u1104','\u1104');

			koreanTailToLead.put('\u11af', '\u1105');
			koreanTailToLead.put('\u11b7', '\u1106');
			koreanTailToLead.put('\u11b8', '\u1107');

			// map to itself
			// koreanTailToLead.put('\u1108', '\u1108');

			koreanTailToLead.put('\u11ba', '\u1109');
			koreanTailToLead.put('\u11bb', '\u110a');
			koreanTailToLead.put('\u11bc', '\u110b');
			koreanTailToLead.put('\u11bd', '\u110c');

			// map to itself
			// koreanTailToLead.put('\u110d', '\u110d');

			koreanTailToLead.put('\u11be', '\u110e');
			koreanTailToLead.put('\u11bf', '\u110f');
			koreanTailToLead.put('\u11c0', '\u1110');
			koreanTailToLead.put('\u11c1', '\u1111');
			koreanTailToLead.put('\u11c2', '\u1112');
		}

		return koreanTailToLead;


	}

	/*
	 * same as Normalizer.normalize(s, Normalizer.Form.NFD) but GWT compatible
	 * 
	 * convert eg \uB458 to \u1103\u116e\u11af
	 * 
	 * and \uB450 to \u1103\u116E
	 */
	public static String flattenKorean(String s) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		boolean lastWasVowel = false;

		for (int i = 0; i < s.length(); i++) {
			char c = convertFromCompatibilityJamo(s.charAt(i), !lastWasVowel);
			if (isKoreanMultiChar(c)) {
				appendKoreanMultiChar(sb, c);
			} else {
				// if a "lead char" follows a vowel, turn into a "tail char"
				if (lastWasVowel && isKoreanLeadChar(c, false)) {
					sb.append(getKoreanLeadToTail().get(Character.valueOf(c))
							.charValue());
				} else {
					sb.append(c);
				}
			}
			lastWasVowel = isKoreanVowelChar(sb.charAt(sb.length() - 1), false);
		}

		return sb.toString();
	}

	// static {
	// for (char i = 0x1100; i <= 0x1112; i++) {
	// for (char j = 0x1161; j <= 0x1175; j++) {
	// String s = i + "" + j;
	// Log.debug(i + " " + j + " " + StringUtil.toHexString(s)
	// + StringUtil.toHexString(unflattenKorean(s).toString()));
	// }
	// }
	//
	// for (char i = 0xac00; i <= 0xD788; i += 1) {
	// String s = i + "";
	// Log.debug(i + " " + StringUtil.toHexString(s) + " "
	// + isKoreanLeadPlusVowelChar(i));
	// }
	// }

	// from 0xac00 to 0xd788, every 28th character is a combination of 2
	// characters not 3
	public static boolean isKoreanLeadPlusVowelChar(char c) {
		if (c >= 0xac00 && c <= 0xd7af) {

			int ch = c - 0xac00;
			if ((ch % 28) == 0) {
				return true;
			}

		}

		return false;
	}

	public static boolean isKoreanMultiChar(char c) {

		if (c >= 0xac00 && c <= 0xd7af) {
			return true;
		}

		return false;
	}

	public static boolean isKoreanLeadChar(char c0, boolean convertJamo) {
		char c = c0;
		if (convertJamo) {
			c = convertFromCompatibilityJamo(c, true);
		}

		if (c >= 0x1100 && c <= 0x1112) {
			return true;
		}

		return false;
	}

	public static boolean isKoreanVowelChar(char c0, boolean convertJamo) {
		char c = c0;
		if (convertJamo) {
			c = convertFromCompatibilityJamo(c, true);
		}

		if (c >= 0x1161 && c <= 0x1175) {
			return true;
		}

		return false;
	}

	public static boolean isKoreanTailChar(char c0, boolean convertJamo) {
		char c = c0;
		if (convertJamo) {
			c = convertFromCompatibilityJamo(c, false);
		}

		if (c >= 0x11a8 && c <= 0x11c2) {
			return true;
		}

		return false;
	}

	/*
	 * 
	 * Does the same as Normalizer.normalize(s, Normalizer.Form.NFKC) but GWT
	 * compatible
	 * 
	 * convert eg \u1103\u116e\u11af to \uB458
	 * 
	 * also converts 2 chars eg \u1103\u116E to \uB450
	 */
	public static StringBuilder unflattenKorean(String str) {

		StringBuilder ret = new StringBuilder();

		char lead = 0;
		char vowel = 0;
		char tail = 0;

		for (int i = 0; i < str.length(); i++) {

			boolean korean = false;

			char c = convertFromCompatibilityJamo(str.charAt(i), lead == 0);

			if (isKoreanLeadChar(c, false)) {
				korean = true;
				if (lead != 0) {
					appendKoreanChar(ret, lead, vowel, tail);
					lead = 0;
					vowel = 0;
					tail = 0;
				}
				lead = c;
			}
			if (isKoreanVowelChar(c, false)) {
				korean = true;
				vowel = c;
			}
			if (isKoreanTailChar(c, false)) {
				korean = true;
				tail = c;
				appendKoreanChar(ret, lead, vowel, tail);
				lead = 0;
				vowel = 0;
				tail = 0;
			}

			if (!korean) {
				// needed for eg
				// "\uD56D\uC131\uC740 \uD56D\uC0C1 \uD63C\uC790 \uC788\uB294
				// \uAC83\uC774 \uC544\uB2C8\uB77C, \uB450 \uAC1C
				// \uC774\uC0C1\uC758"
				// to stop order changing
				if (lead != 0) {
					appendKoreanChar(ret, lead, vowel, tail);
					lead = 0;
					vowel = 0;
					tail = 0;

				}
				ret.append(c);
			}
		}

		// make sure last char done!
		if (lead != 0) {
			appendKoreanChar(ret, lead, vowel, tail);
		}

		return ret;
	}

	private static void appendKoreanChar(StringBuilder ret, char lead,
			char vowel, char tail) {

		int lead0 = lead - 0x1100 + 1;
		int vowel0 = vowel - 0x1161 + 1;
		int tail0 = tail == 0 ? 0 : tail - 0x11a8 + 1;

		// http://gernot-katzers-spice-pages.com/var/korean_hangul_unicode.html
		char unicode = (char) (tail0 + (vowel0 - 1) * 28 + (lead0 - 1) * 588
				+ 44032);

		ret.append(unicode);
	}

	/**
	 * https://en.wikipedia.org/wiki/Hangul_Compatibility_Jamo
	 * 
	 * @param ch
	 * @param lead
	 * @return
	 */
	public static char convertFromCompatibilityJamo(char ch, boolean lead) {
		switch (ch) {
		case '\u3131':
			return lead ? '\u1100' : '\u11a8';

		case '\u3132':
			return lead ? '\u1101' : '\u11a9';

		case '\u3133':
			return '\u11aa';

		case '\u3134':
			return lead ? '\u1102' : '\u11ab';

		case '\u3135':
			return '\u11ac';

		case '\u3136':
			return '\u11ad';

		case '\u3137':
			return lead ? '\u1103' : '\u11ae';

		case '\u3138':
			return '\u1104';

		case '\u3139':
			return lead ? '\u1105' : '\u11af';

		case '\u313a':
			return '\u11b0';

		case '\u313b':
			return '\u11b1';

		case '\u313c':
			return '\u11b2';

		case '\u313d':
			return '\u11b3';

		case '\u313e':
			return '\u11b4';

		case '\u313f':
			return '\u11b5';

		case '\u3140':
			return '\u11b6';

		case '\u3141':
			return lead ? '\u1106' : '\u11b7';

		case '\u3142':
			return lead ? '\u1107' : '\u11b8';

		case '\u3143':
			return '\u1108';

		case '\u3144':
			return lead ? '\u1121' : '\u11b9';

		case '\u3145':
			return lead ? '\u1109' : '\u11ba';

		case '\u3146':
			return lead ? '\u110a' : '\u11bb';

		case '\u3147':
			return lead ? '\u110b' : '\u11bc';

		case '\u3148':
			return lead ? '\u110c' : '\u11bd';

		case '\u3149':
			return '\u110d';

		case '\u314a':
			return lead ? '\u110e' : '\u11be';

		case '\u314b':
			return lead ? '\u110f' : '\u11bf';

		case '\u314c':
			return lead ? '\u1110' : '\u11c0';

		case '\u314d':
			return lead ? '\u1111' : '\u11c1';

		case '\u314e':
			return lead ? '\u1112' : '\u11c2';

		case '\u314f':
			return '\u1161';

		case '\u3150':
			return '\u1162';

		case '\u3151':
			return '\u1163';

		case '\u3152':
			return '\u1164';

		case '\u3153':
			return '\u1165';

		case '\u3154':
			return '\u1166';

		case '\u3155':
			return '\u1167';

		case '\u3156':
			return '\u1168';

		case '\u3157':
			return '\u1169';

		case '\u3158':
			return '\u116a';

		case '\u3159':
			return '\u116b';

		case '\u315a':
			return '\u116c';

		case '\u315b':
			return '\u116d';

		case '\u315c':
			return '\u116e';

		case '\u315d':
			return '\u116f';

		case '\u315e':
			return '\u1170';

		case '\u315f':
			return '\u1171';

		case '\u3160':
			return '\u1172';

		case '\u3161':
			return '\u1173';

		case '\u3162':
			return '\u1174';

		case '\u3163':
			return '\u1175';

		}

		return ch;
	}

	/*
	 * http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html
	 * http://gernot-katzers-spice-pages.com/var/korean_hangul_unicode.html
	 */
	private static void appendKoreanMultiChar(StringBuilder sBuilder, char c) {
		char tail = (char) (0x11a7 + (c - 44032) % 28);
		char vowel = (char) (0x1161
				+ ((c - 44032 - (tail - 0x11a7)) % 588) / 28);
		char lead = (char) (0x1100 + (c - 44032) / 588);
		// Application.debug(Util.toHexString(c)+" decoded to
		// "+Util.toHexString(lead)+Util.toHexString(vowel)+Util.toHexString(tail));
		sBuilder.append(lead);
		sBuilder.append(vowel);
		if (!isKoreanLeadPlusVowelChar(c)) {
			sBuilder.append(tail);
		}
	}

	public static String unmergeDoubleCharacterToLeadTail(char c) {

		switch (c) {

		case '\u3149':
		case '\u110d':
			return "\u11bd\u110c";

		case '\u3143':
		case '\u1108':
			return "\u11b8\u1107";

		case '\u3132':
		case '\u1101':
		case '\u11a9':
			return "\u11a8\u1101";

		case '\u3133':
		case '\u11aa':
			return "\u11a8\u1109";

		case '\u3135':
		case '\u11ac':
			return "\u11ab\u110c";

		case '\u3136':
		case '\u11ad':
			return "\u11ab\u1112";

		case '\u313a':
		case '\u11b0':
			return "\u11af\u1100";

		case '\u313b':
		case '\u11b1':
			return "\u11af\u1106";

		case '\u313c':
		case '\u11b2':
			return "\u11af\u1107";

		case '\u313d':
		case '\u11b3':
			return "\u11af\u1109";

		case '\u313e':
		case '\u11b4':
			return "\u11af\u1110";

		case '\u313f':
		case '\u11b5':
			return "\u11af\u1111";

		case '\u3140':
		case '\u11b6':
			return "\u11af\u1112";

		case '\u3144':
		case '\u1121':
		case '\u11b9':
			return "\u11b8\u1109";

		case '\u110A':
		case '\u11BB':
		case '\u3145':
			// tail + lead
			return "\u11ba\u1109";

		}

		return c + "";
	}


	// static {
	// String s = "\ub450";
	// s = flattenKorean(s);
	// Log.debug("length = " + s.length());
	// for (int i = 0; i < s.length(); i++) {
	// Log.debug(StringUtil.toHexString(s.charAt(i)));
	// }
	//
	// s = "\u1103\u116E";
	// s = unflattenKorean(s).toString();
	// Log.debug("\u1103\u116E goes to " + StringUtil.toHexString(s));
	// }
	
	public static String mergeDoubleCharacters(String str) {
		return mergeDoubleCharacters(str, true);
	}

	/*
	 * avoid having to press shift by merging eg \u1100\u1100 to \u1101
	 * http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html
	 */
	public static String mergeDoubleCharacters(String str, boolean lead) {

		if (str.length() < 2) {
			return str;
		}

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		char c, c2;

		for (int i = 0; i < str.length() - 1; i++) {
			int offset = 1;
			switch (c = str.charAt(i)) {
			
			case '\u11ab':
			case '\u1102':
				switch (str.charAt(i + 1)) {
				case '\u110c':
				case '\u11bd':
					sb.append('\u11ac');
					i++;
					break;
				case '\u1112':
				case '\u11c2':
					sb.append('\u11ad');
					i++;
					break;
				default:
					sb.append(c);
						
				}
				break;
			
			case '\u1105':
			case '\u11af':
				switch (str.charAt(i + 1)) {
				case '\u3131':
				case '\u1100':
				case '\u11a8':
					sb.append('\u11b0');
					i++;
					break;
				case '\u3141':
				case '\u1106':
				case '\u11b7':
					sb.append('\u11b1');
					i++;
					break;
				case '\u3142':
				case '\u11b8':
				case '\u1107':
					sb.append('\u11b2');
					i++;
					break;
				case '\u3145':
				case '\u1109':
				case '\u11ba':
					sb.append('\u11b3');
					i++;
					break;
				case '\u314c':
				case '\u1110':
				case '\u11c0':
					sb.append('\u11b4');
					i++;
					break;
				case '\u314d':
				case '\u1111':
				case '\u11c1':
					sb.append('\u11b5');
					i++;
					break;
				case '\u314e':
				case '\u1112':
				case '\u11c2':
					sb.append('\u11b6');
					i++;
					break;
				default:
					sb.append(c);
				}
				break;

			case '\u1161': // these character are "doubled" by adding 2 to their
				// Unicode value
			case '\u1162':
			case '\u1165':
			case '\u1166':
				offset++;
				// fall through
				// case '\u1100' : // these character are "doubled" by adding 1
				// to their Unicode value
			case '\u1103':
				// case '\u1107' :
			case '\u1109':
			case '\u110c':
			case '\u11a8':
			case '\u11ba':
				if (str.charAt(i + 1) == c) {
					sb.append((char) (c + offset)); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1169':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1161') {
					sb.append('\u116a'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1162') {
					sb.append('\u116b'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1175') {
					sb.append('\u116c'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1169') {
					sb.append('\u116d'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			// case '\u1105':
			// case '\u11af':
			// c2 = str.charAt(i + 1);
			// if (c2 == '\u1100') {
			// sb.append('\u11b0'); // eg \u1101 ie doubled char
			// i++;
			// } else if (c2 == '\u1106') {
			// sb.append('\u11b1'); // eg \u1101 ie doubled char
			// i++;
			// } else if (c2 == '\u1107') {
			// sb.append('\u11b2'); // eg \u1101 ie doubled char
			// i++;
			// } else if (c2 == '\u1109') {
			// sb.append('\u11b3'); // eg \u1101 ie doubled char
			// i++;
			// } else if (c2 == '\u1110') {
			// sb.append('\u11b4'); // eg \u1101 ie doubled char
			// i++;
			// } else if (c2 == '\u1112') {
			// sb.append('\u11b6'); // eg \u1101 ie doubled char
			// i++;
			// } else {
			// sb.append(c);
			// }
			// break;
			case '\u116e':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1165') {
					sb.append('\u116f'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1166') {
					sb.append('\u1170'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1175') {
					sb.append('\u1171'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u116e') {
					sb.append('\u1172'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1173':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1175') {
					sb.append('\u1174'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1100':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1100') {
					// assume we want lead \u1101 not tail \u11a9
					// eg \u3131 + \u3131 + \u314F -> \uAE4C
					sb.append('\u1101'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1109') {
					sb.append('\u11aa'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
//			case '\u1102':
//				c2 = str.charAt(i + 1);
//				if (c2 == '\u110c') {
//					sb.append('\u11ac'); // eg \u1101 ie doubled char
//					i++;
//				} else if (c2 == '\u1112') {
//					sb.append('\u11ad'); // eg \u1101 ie doubled char
//					i++;
//				} else {
//					sb.append(c);
//				}
//				break;
			case '\u1111':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1111') {
					sb.append('\u11b5'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u1107':
				c2 = str.charAt(i + 1);
				if (c2 == '\u1109') {
					sb.append('\u11b9'); // eg \u1101 ie doubled char
					i++;
				} else if (c2 == '\u1107') {
					sb.append('\u1108'); // eg \u1101 ie doubled char
					i++;
				} else {
					sb.append(c);
				}
				break;
			case '\u11b8':
				c2 = str.charAt(i + 1);
				if (c2 == '\u11ba') {
					sb.append('\u11b9');
					i++;
				}
				break;
			default:
				sb.append(c);
			}
			if (i == str.length() - 2) {
				sb.append(str.charAt(str.length() - 1));
			}

		}

		return sb.toString();
	}

	/**
	 * tries to combine lastChar+newChar as a single char or two chars if
	 * necessary
	 * 
	 * not as simple as doing this!! unflatten(flatten(lastChar)+newChar)
	 * 
	 * @param lastChar
	 *            already typed character
	 * @param newChar
	 *            new character just typed
	 * @return {char, char2} if 2 chars are needed, otherwise just {char, 0}
	 */
	public static char[] checkMerge(char lastChar, char newChar) {

		char[] ret = { lastChar, newChar };

		// case 1
		// we already have Jamo lead + vowel as single unicode

		if (Korean.isKoreanLeadPlusVowelChar(lastChar)
				&& Korean.isKoreanTailChar(newChar, true)) {

			String strToFlatten = Korean.flattenKorean(lastChar + "") + ""
					+ newChar;

			String replaceChar = Korean.unflattenKorean(strToFlatten)
					.toString();

			// System.err.println("flattening " + strToFlatten + " "
			// + toHexString(strToFlatten));
			//
			// System.err.println("need to replace " + lastChar + " "
			// + toHexString(lastChar) + " with " + replaceChar + " "
			// + toHexString(replaceChar));

			char c = replaceChar.charAt(0);

			ret[0] = c;
			ret[1] = 0;
			return ret;

		}

		// case 2
		// we already have just Jamo lead char as single unicode

		if (Korean.isKoreanLeadChar(lastChar, true)
				&& Korean.isKoreanVowelChar(newChar, true)) {
			String replaceChar = Korean.unflattenKorean(lastChar + "" + newChar)
					.toString();
			// System.err.println("need to replace " + lastChar + " "
			// + toHexString(lastChar) + " with " + replaceChar + " "
			// + toHexString(replaceChar));

			char c = replaceChar.charAt(0);

			ret[0] = c;
			ret[1] = 0;
			return ret;

		}

		// case 3
		// character typed twice (instead of pressing <Shift>)
		String merged = Korean.mergeDoubleCharacters(
				Korean.flattenKorean(lastChar + "" + newChar));

		// System.err.println(
		// lastChar + "" + newChar + " " + merged + " " + merged.length());

		if (merged.length() == 1) {

			char c = merged.charAt(0);

			ret[0] = c;
			ret[1] = 0;
			return ret;

		}

		// case 4
		// we have something like
		// \u3141 \u3163 \u3142 \u315C \u3134
		// which has been grouped as
		// (\u3141 \u3163 \u3142) + \u315C
		// but when \u3134 is typed it needs to change to
		// (\u3141 \u3163) + (\u3142 \u315C \u3134)
		// ie "\u3134" needs to change from tail (\u11ab) to lead (\u1102)

		String lastCharFlat = Korean.flattenKorean(lastChar + "");

		if (lastCharFlat.length() == 3
				&& Korean.isKoreanVowelChar(newChar, true)) {

			// System.err.println("case 4");

			// not needed, useful for debugging
			// newChar = Korean.convertFromCompatibilityJamo(newChar,
			// false);

			char lastCharFlat2 = lastCharFlat.charAt(2);
			// System.err.println("lastCharFlat2 = " + lastCharFlat2);

			char newLastChar = Korean
					.unflattenKorean(lastCharFlat.substring(0, 2)).charAt(0);

			char newNewChar = Korean.unflattenKorean(
					Korean.tailToLead(lastCharFlat.charAt(2)) + "" + newChar)
					.charAt(0);

			String unmergedChar2 = isKoreanVowelChar(newChar, true)
					? unmergeDoubleCharacterToLeadTail(lastCharFlat2) : "";

			// case 4a
			// tail doubled char needs to be undoubled and split across 2 chars
			// testEditor("\u314E\u314F\u3145\u3145\u314F\u3147",
			// "\uD56B\uC0C1");

			if (unmergedChar2.length() == 2) {

				char ch1 = unmergedChar2.charAt(0);
				char ch2 = unmergedChar2.charAt(1);

				// System.err.println("trying to undouble " + lastCharFlat2
				// + " as " + ch1 + " " + ch2);

				newLastChar = Korean
						.unflattenKorean(lastCharFlat.substring(0, 2) + ch1)
						.charAt(0);

				newNewChar = Korean
						.unflattenKorean(ch2 + "" + newChar)
						.charAt(0);

			}

			ret[0] = newLastChar;
			ret[1] = newNewChar;
			return ret;

		}

		// case5: a tailed char is doubled
		// entered as two key presses
		// eg \u3131 \u314F \u3142 \u3145 needs to give \uAC12

		if (lastCharFlat.length() == 3
				&& Korean.isKoreanTailChar(newChar, true)) {

			// System.err.println("case 5");

			// System.err.println("case 5");

			newChar = Korean.convertFromCompatibilityJamo(newChar, false);

			char lastChar2 = lastCharFlat.charAt(2);

			// if this is length 1, merge succeeded
			String doubleCheck = Korean
					.mergeDoubleCharacters(lastChar2 + "" + newChar);

			// System.err.println("doubleCheck = " + doubleCheck + " "
			// + doubleCheck.length());

			if (doubleCheck.length() == 1) {
				// System.err.println("merge check passed");

				newChar = Korean
						.unflattenKorean(
								lastCharFlat.substring(0, 2) + "" + doubleCheck)
						.charAt(0);

				ret[0] = newChar;
				ret[1] = 0;
				return ret;

			}

		}

		// case 6
		// a vowel character is a "doubled" char
		// case 1
		// we already have Jamo lead + vowel as single unicode

		// System.err.println("Korean.isKoreanLeadPlusVowelChar(lastChar) = "
		// + Korean.isKoreanLeadPlusVowelChar(lastChar));
		// System.err.println("Korean.isKoreanVowelChar(newChar, true) = "
		// + Korean.isKoreanVowelChar(newChar, true));

		if (Korean.isKoreanLeadPlusVowelChar(lastChar)
				&& Korean.isKoreanVowelChar(newChar, true)) {

			char lastChar1 = lastCharFlat.charAt(1);

			// if this is length 1, merge succeeded
			String doubleCheck = Korean.mergeDoubleCharacters(lastChar1 + ""
					+ Korean.convertFromCompatibilityJamo(newChar, true));

			// System.err.println("doubleCheck = " + doubleCheck);

			if (doubleCheck.length() == 1) {
				// System.err.println("merge check 2 passed");

				newChar = Korean
						.unflattenKorean(
								lastCharFlat.charAt(0) + "" + doubleCheck)
						.charAt(0);

				ret[0] = newChar;
				ret[1] = 0;
				return ret;

			}

		}

		return ret;

	}

	public static boolean isCompatibilityChar(char ch) {
		return ch >= '\u3131' && ch <= '\u318e';
	}

	public static boolean isSingleKoreanChar(char ch) {
		
		return isKoreanLeadChar(ch, true) || isKoreanVowelChar(ch, true)
				|| isKoreanTailChar(ch, true);
		
		
	}

}
