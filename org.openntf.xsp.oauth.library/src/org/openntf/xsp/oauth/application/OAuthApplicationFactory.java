/*
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
package org.openntf.xsp.oauth.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import org.openntf.xsp.oauth.Activator;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.ApplicationFactoryImpl;

public class OAuthApplicationFactory extends ApplicationFactoryImpl {
	private final static boolean _debug = Activator._debug;
	static {
		if (_debug) {
			System.out.println(OAuthApplicationFactory.class.getName() + " loaded");
		}
	}

	public OAuthApplicationFactory() {
		if (_debug) {
			System.out.println(getClass().getName() + " created");
		}
	}

	public OAuthApplicationFactory(final ApplicationFactory factory) {
		super(factory);
		if (_debug) {
			System.out.println(getClass().getName() + " created with delegate");
		}
	}

	@Override
	protected ApplicationEx createApplicationInstance(final Application app) {
		return new OAuthApplicationEx(app);
	}
}
