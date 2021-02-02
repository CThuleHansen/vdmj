/*******************************************************************************
 *
 *	Copyright (c) 2016 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.fujitsu.vdmj.tc.types;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.tc.types.visitors.TCTypeVisitor;

public class TCSeq1Type extends TCSeqType
{
	private static final long serialVersionUID = 1L;

	public TCSeq1Type(LexLocation location, TCType type)
	{
		super(location, type);
	}

	@Override
	public String toDisplay()
	{
		return "seq1 of (" + seqof + ")";
	}

	@Override
	public boolean equals(Object other)
	{
		other = deBracket(other);

		if (other.getClass().equals(TCSeq1Type.class))
		{
			TCSeq1Type os = (TCSeq1Type)other;
			return seqof.equals(os.seqof);
		}

		return false;
	}

	@Override
	public <R, S> R apply(TCTypeVisitor<R, S> visitor, S arg)
	{
		return visitor.caseSeq1Type(this, arg);
	}
}