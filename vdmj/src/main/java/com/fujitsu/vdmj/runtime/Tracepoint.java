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

package com.fujitsu.vdmj.runtime;

import com.fujitsu.vdmj.debug.DebugLink;
import com.fujitsu.vdmj.in.expressions.INBreakpointExpression;
import com.fujitsu.vdmj.lex.LexLocation;

/**
 * A breakpoint where something is displayed.
 */
public class Tracepoint extends Breakpoint
{
	private static final long serialVersionUID = 1L;

	public Tracepoint(LexLocation location, int number, String trace) throws Exception
	{
		super(location, number, trace);
		
		if (condition instanceof INBreakpointExpression)
		{
			throw new Exception("Trace cannot use hit-count expressions");
		}
	}

	@Override
	public void check(LexLocation execl, Context ctxt)
	{
		location.hit();
		hits++;
		DebugLink.getInstance().tracepoint(ctxt, this);
	}

	@Override
	public String toString()
	{
		return "trace [" + number + "] " +
				(trace == null ? "" : "show \"" + trace + "\" ") +
				super.toString();
	}
}
