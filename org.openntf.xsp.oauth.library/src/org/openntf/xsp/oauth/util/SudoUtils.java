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
package org.openntf.xsp.oauth.util;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import lotus.domino.Session;
import com.ibm.domino.napi.c.NotesUtil;
import com.ibm.domino.napi.c.xsp.XSPNative;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.FacesUtil;

public class SudoUtils {
	public interface SudoCallback {
		public Object onException(Exception e);

		public Object run(Session session);
	}

	public static Session getSessionAs(final String userName) {
		Session result = null;
		try {
			result = AccessController.doPrivileged(new PrivilegedExceptionAction<lotus.domino.Session>() {
				public lotus.domino.Session run() throws Exception {
					final long hList = NotesUtil.createUserNameList(userName);
					return XSPNative.createXPageSession(userName, hList, true, false);
				}
			});
		} catch (final Exception e) {
			result = ExtLibUtil.getCurrentSession();
		}
		return result;
	}

	public static Object runOnBehalfOf(final String userName, final MethodBinding callback) {
		return runOnBehalfOf(userName, new SudoCallback() {
			public Object onException(final Exception e) {
				e.printStackTrace();
				return null;
			}

			public Object run(final Session session) {
				Object methodResult = null;
				try {
					methodResult = callback.invoke(FacesContext.getCurrentInstance(), null);
				} catch (final Exception e) {
					onException(e);
				}
				return methodResult;
			}
		});
	}

	public static Object runOnBehalfOf(final String userName, final SudoCallback callback) {
		Object result = null;
		final Map<String, Object> requestScope = ExtLibUtil.getRequestScope();
		try {
			final Session s = getSessionAs(userName);
			requestScope.put("sessionAsSudo", s);
			result = callback.run(s);
		} catch (final Exception e) {
			FacesUtil.addErrorMessage(e.getMessage(), e.getMessage());
			result = callback.onException(e);
		} finally {
			requestScope.remove("sessionAsSudo");
		}
		return result;
	}
}
