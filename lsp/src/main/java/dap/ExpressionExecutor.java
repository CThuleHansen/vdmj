/*******************************************************************************
 *
 *	Copyright (c) 2021 Nick Battle.
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

package dap;

import java.io.IOException;

import com.fujitsu.vdmj.runtime.Interpreter;

import json.JSONObject;

public class ExpressionExecutor extends AsyncExecutor
{
	private final String expression;
	private String answer;

	public ExpressionExecutor(String id, DAPRequest request, String expression)
	{
		super(id, request);
		this.expression = expression;
	}

	@Override
	protected void head()
	{
		// Nothing
	}

	@Override
	protected void exec() throws Exception
	{
		answer = Interpreter.getInstance().execute(expression).toString();
	}

	@Override
	protected void tail(double time) throws IOException
	{
		answer = "= " + answer + "\nExecuted in " + time + " secs.\n";
		server.writeMessage(new DAPResponse(request, true, null,
				new JSONObject("result", answer, "variablesReference", 0)));
	}

	@Override
	protected void error(Exception e) throws IOException
	{
		server.writeMessage(new DAPResponse(request, false, e.getMessage(), null));
		server.writeMessage(new DAPResponse("output", new JSONObject("output", "Execution terminated.")));
	}
}
