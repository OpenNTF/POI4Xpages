/*
 * © Copyright WebGate Consulting AG, 2012
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
package biz.webgate.dominoext.poi;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	public static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		try {
			String s = AccessController
					.doPrivileged(new PrivilegedAction<String>() {
						public String run() {
							Object o = Activator.getContext().getBundle()
									.getHeaders().get("Bundle-Version"); // $NON-NLS-1$
							if (o != null) {
								return o.toString();
							}
							return null;
						}
					});
			if (s != null) {
				biz.webgate.dominoext.poi.utils.Activator.setPoiVersion(s);
			} else {
				biz.webgate.dominoext.poi.utils.Activator.setPoiVersion("<POI VERSION not Initialized?");
			}
		} catch (SecurityException ex) {
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
