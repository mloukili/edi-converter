/*
 *  Copyright 2005, 2006 by BerryWorks Software, LLC
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.beesphere.edi.reader;

/**
 * A token noted by EDITokenizer.
 */
public interface Token {

	enum TokenType {
		UNKNOWN, SEGMENT_START, SIMPLE, EMPTY, SUB_ELEMENT, SUB_EMPTY, SEGMENT_END, END_OF_DATA
	}

	/**
	 * Gets the type of the token.
	 * 
	 * @return SEGMENT_START, SIMPLE, SUB_ELEMENT, ...
	 */
	public TokenType getType();

	/**
	 * Is true for the first subelement in a series of subelements.
	 * 
	 * @return boolean
	 */
	public boolean isFirst();

	/**
	 * Is true for the last subelemnt in a series of subelements.
	 * 
	 * @return boolean
	 */
	public boolean isLast();

	/**
	 * Gets the ordinal position of the token in the segment, origin 0.
	 * 
	 * @return The index value
	 */
	public int getIndex();

	/**
	 * Gets the ordinal position of a subelement within a series of subelements
	 * token in the segment.
	 * 
	 * @return int position origin 0
	 */
	public int getSubIndex();

	/**
	 * Gets the value of a SIMPLE token. <pr> If this token is of type
	 * SEGMENT_START, the value of getSegmentType() is returned.
	 * 
	 * @return The value value
	 */
	public String getValue();

	/**
	 * Gets the same thing as <code>getValue</code>, returning it as a
	 * <code>char[]</code>.
	 * 
	 * @return The valueChars value
	 */
	public char[] getValueChars();

	/**
	 * Returns true if the value of this token equals the argument.
	 * 
	 * @param v
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean valueEquals(String v);

	/**
	 * Gets the value of the first token in the segment.
	 * 
	 * @return The segmentType value
	 */
	public String getSegmentType();

	/**
	 * For a token of type COMPOSITE, returns an array of tokens corresponding
	 * to the first-level subtotkens.
	 * 
	 * @return The subTokens value
	 */
	public Token[] getSubTokens();

	/**
	 * Returns a String concatenation of the segment type and a two-digit (or
	 * more) representation of the token's getIndex() value.
	 * 
	 * @return The elementIdS value
	 */
	public String getElementId();

	/**
	 * For SIMPLE tokens that are part of a repeating sequence, returns the
	 * ordinal position within the sequence. Otherwise returns 0.
	 * 
	 * @return The repeatCount value
	 */
	public int getRepeatCount();
}
