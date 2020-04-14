
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
 *
 *
 ******************************************************************************/

package com.fujitsu.vdmj.in.expressions;

import com.fujitsu.vdmj.in.patterns.INMultipleBind;
import com.fujitsu.vdmj.in.patterns.INMultipleBindList;
import com.fujitsu.vdmj.in.patterns.INPattern;
import com.fujitsu.vdmj.lex.LexLocation;
import com.fujitsu.vdmj.runtime.Context;
import com.fujitsu.vdmj.runtime.ValueException;
import com.fujitsu.vdmj.tc.lex.TCNameList;
import com.fujitsu.vdmj.values.BooleanValue;
import com.fujitsu.vdmj.values.NameValuePair;
import com.fujitsu.vdmj.values.NameValuePairList;
import com.fujitsu.vdmj.values.Quantifier;
import com.fujitsu.vdmj.values.QuantifierList;
import com.fujitsu.vdmj.values.Value;
import com.fujitsu.vdmj.values.ValueList;

public class INExistsExpression extends INExpression
{
	private static final long serialVersionUID = 1L;
	public final INMultipleBindList bindList;
	public final INExpression predicate;

	public INExistsExpression(LexLocation location, INMultipleBindList bindList, INExpression predicate)
	{
		super(location);
		this.bindList = bindList;
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		return "(exists " + bindList + " & " + predicate + ")";
	}

	@Override
	public INExpression findExpression(int lineno)
	{
		INExpression found = super.findExpression(lineno);
		if (found != null) return found;

		return predicate.findExpression(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (INMultipleBind mb: bindList)
			{
				ValueList bvals = mb.getBindValues(ctxt, false);

				for (INPattern p: mb.plist)
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init(ctxt, true);

			while (quantifiers.hasNext())
			{
				Context evalContext = new Context(location, "exists", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches && predicate.eval(evalContext).boolValue(ctxt))
				{
					return new BooleanValue(true);
				}
			}
		}
	    catch (ValueException e)
	    {
	    	abort(e);
	    }

		return new BooleanValue(false);
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		ValueList list = new ValueList();

		for (INMultipleBind mb: bindList)
		{
			list.addAll(mb.getValues(ctxt));
		}

		list.addAll(predicate.getValues(ctxt));
		return list;
	}

	@Override
	public TCNameList getOldNames()
	{
		TCNameList list = new TCNameList();

		for (INMultipleBind mb: bindList)
		{
			list.addAll(mb.getOldNames());
		}

		list.addAll(predicate.getOldNames());
		return list;
	}

	@Override
	public <R, S> R apply(INExpressionVisitor<R, S> visitor, S arg)
	{
		return visitor.caseExistsExpression(this, arg);
	}
}
