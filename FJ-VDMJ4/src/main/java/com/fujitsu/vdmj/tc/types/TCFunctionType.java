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
import com.fujitsu.vdmj.tc.definitions.TCAccessSpecifier;
import com.fujitsu.vdmj.tc.definitions.TCTypeDefinition;
import com.fujitsu.vdmj.tc.lex.TCNameSet;
import com.fujitsu.vdmj.typechecker.Environment;
import com.fujitsu.vdmj.typechecker.TypeCheckException;
import com.fujitsu.vdmj.util.Utils;

public class TCFunctionType extends TCType
{
	private static final long serialVersionUID = 1L;
	public TCTypeList parameters;
	public TCType result;
	public final boolean partial;
	public Boolean instantiated = null;		// Null => not polymorphic

	public TCFunctionType(LexLocation location, TCTypeList parameters, boolean partial, TCType result)
	{
		super(location);
		this.parameters = parameters;
		this.result = result;
		this.partial = partial;
	}

	public TCFunctionType getPreType()
	{
		TCFunctionType type =
			new TCFunctionType(location, parameters, false, new TCBooleanType(location));
		type.definitions = definitions;
		return type;
	}

	public TCFunctionType getPostType()
	{
		TCTypeList params = new TCTypeList();
		params.addAll(parameters);
		params.add(result);
		TCFunctionType type =
			new TCFunctionType(location, params, false, new TCBooleanType(location));
		type.definitions = definitions;
		return type;
	}

	public TCFunctionType getMeasureType(TCType result)
	{
		TCFunctionType type = new TCFunctionType(location, parameters, false, result);
		type.definitions = definitions;
		return type;
	}

	public TCFunctionType getCurriedPreType(boolean isCurried)
	{
		if (isCurried && result instanceof TCFunctionType)
		{
			TCFunctionType ft = (TCFunctionType)result;
			TCFunctionType type = new TCFunctionType(location,
				parameters, false, ft.getCurriedPreType(isCurried));
			type.definitions = definitions;
			return type;
		}
		else
		{
			return getPreType();
		}
	}

	public TCFunctionType getCurriedPostType(boolean isCurried)
	{
		if (isCurried && result instanceof TCFunctionType)
		{
			TCFunctionType ft = (TCFunctionType)result;
			TCFunctionType type = new TCFunctionType(location,
				parameters, false, ft.getCurriedPostType(isCurried));
			type.definitions = definitions;
			return type;
		}
		else
		{
			return getPostType();
		}
	}

	@Override
	public boolean isFunction(LexLocation from)
	{
		return true;
	}

	@Override
	public TCFunctionType getFunction()
	{
		return this;
	}

	@Override
	public String toDisplay()
	{
		String params = (parameters.isEmpty() ?
						"()" : Utils.listToString(parameters, " * "));
		return "(" + params + (partial ? " -> " : " +> ") + result + ")";
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }

		for (TCType type: parameters)
		{
			type.unResolve();
		}

		result.unResolve();
	}

	@Override
	public TCFunctionType typeResolve(Environment env, TCTypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		TCTypeList fixed = new TCTypeList();
		TypeCheckException problem = null;

		for (TCType type: parameters)
		{
			try
			{
				fixed.add(type.typeResolve(env, root));
			}
			catch (TypeCheckException e)
			{
				if (problem == null)
				{
					problem = e;
				}
				else
				{
					// Add extra messages to the exception for each parameter
					problem.addExtra(e);
				}
				
				fixed.add(new TCUnknownType(location));	// Parameter count must be right
			}
		}

		try
		{
			parameters = fixed;
			result = result.typeResolve(env, root);
		}
		catch (TypeCheckException e)
		{
			if (problem == null)
			{
				problem = e;
			}
			else
			{
				problem.addExtra(e);
			}
		}
		
		if (problem != null)
		{
			unResolve();
			throw problem;
		}
		
		return this;
	}

	@Override
	public boolean equals(Object other)
	{
		other = deBracket(other);

		if (!(other instanceof TCFunctionType))
		{
			return false;
		}

		TCFunctionType fo = (TCFunctionType)other;
		return (partial == fo.partial &&
				result.equals(fo.result) &&
				parameters.equals(fo.parameters));
	}

	@Override
	public int hashCode()
	{
		return parameters.hashCode() + result.hashCode();
	}

	@Override
	public boolean narrowerThan(TCAccessSpecifier accessSpecifier)
	{
		for (TCType t: parameters)
		{
			if (t.narrowerThan(accessSpecifier))
			{
				return true;
			}
		}

		return result.narrowerThan(accessSpecifier);
	}
	
	@Override
	public TCTypeList getComposeTypes()
	{
		TCTypeList list = new TCTypeList();
		list.addAll(parameters.getComposeTypes());
		list.addAll(result.getComposeTypes());
		return list;
	}
	
	@Override
	public TCNameSet getFreeVariables(Environment env)
	{
		TCNameSet names = parameters.getFreeVariables(env);
		names.addAll(result.getFreeVariables(env));
		return names;
	}
}
