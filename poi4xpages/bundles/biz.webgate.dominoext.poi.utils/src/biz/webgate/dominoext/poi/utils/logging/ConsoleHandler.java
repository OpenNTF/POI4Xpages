/*
 * � Copyright WebGate Consulting AG, 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package biz.webgate.dominoext.poi.utils.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ConsoleHandler extends Handler {

	private String m_CallerClass = "";
	private Level m_Level;

	public ConsoleHandler(String callerClass, Level nLevel) {
		super();
		m_CallerClass = callerClass;
		m_Level = nLevel;
	}

	@Override
	public void setLevel(Level newLevel) {
		m_Level = newLevel;
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord logEvent) {
		if (logEvent.getLevel().intValue() >= m_Level.intValue())
			System.out.println(logEvent.getLevel().getLocalizedName() + ": "
					+ logEvent.getMessage() + " (" + m_CallerClass + ")");
	}


}
