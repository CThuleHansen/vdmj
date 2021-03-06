/*******************************************************************************
 *
 *	Copyright (c) 2008, 2016 Fujitsu Services Ltd.
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

package com.fujitsu.vdmj.po.statements;

import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.po.expressions.POExpression;
import com.fujitsu.vdmj.po.statements.visitors.POStatementVisitor;

public class POCyclesStatement extends POStatement
{
	private static final long serialVersionUID = 1L;
	public final POExpression cycles;
	public final POStatement statement;

	public POCyclesStatement(LexLocation location, POExpression cycles, POStatement stmt)
	{
		super(location);
		this.cycles = cycles;
		this.statement = stmt;
	}

	@Override
	public String toString()
	{
		return "cycles (" + cycles + ") " + statement;
	}

	@Override
	public <R, S> R apply(POStatementVisitor<R, S> visitor, S arg)
	{
		return visitor.caseCyclesStatement(this, arg);
	}
}
