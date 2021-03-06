package org.pac4j.springframework.security.web;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.http.adapter.J2ENopHttpActionAdapter;
import org.pac4j.springframework.security.profile.SpringSecurityProfileManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This filter finishes the login process for an indirect client, based on the {@link #callbackLogic}.</p>
 *
 * <p>The configuration can be provided via setters:</p>
 * <ul>
 *     <li><code>{@link #setConfig(Config)}</code> (security configuration)</li>
 *     <li><code>{@link #setDefaultUrl(String)}</code> (default url after login if none was requested)</li>
 *     <li><code>{@link #setSaveInSession(Boolean)}</code> (whether the profile should be saved into the session)</li>
 *     <li><code>{@link #setMultiProfile(Boolean)}</code> (whether multiple profiles should be kept)</li>
 *     <li><code>{@link #setRenewSession(Boolean)}</code> (whether the session must be renewed after login)</li>
 *     <li><code>{@link #setDefaultClient(String)}</code> (the default client if none is provided on the URL)</li>
 * </ul>
 *
 * <p>This filter only applies if the suffix is blank or if the current request URL ends with the suffix (by default: <code>/callback</code>).</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CallbackFilter extends AbstractPathFilter {

    public final static String DEFAULT_CALLBACK_SUFFIX = "/callback";

    private CallbackLogic<Object, J2EContext> callbackLogic;

    private Config config;

    private String defaultUrl;

    private Boolean saveInSession;

    private Boolean multiProfile;

    private Boolean renewSession;

    private String defaultClient;

    public CallbackFilter() {
        callbackLogic = new DefaultCallbackLogic<>();
        setSuffix(DEFAULT_CALLBACK_SUFFIX);
        ((DefaultCallbackLogic<Object, J2EContext>) callbackLogic).setProfileManagerFactory(SpringSecurityProfileManager::new);
    }

    public CallbackFilter(final Config config) {
        this();
        this.config = config;
    }

    public CallbackFilter(final Config config, final String defaultUrl) {
        this(config);
        this.defaultUrl = defaultUrl;
    }

    public CallbackFilter(final Config config, final String defaultUrl, final boolean multiProfile) {
        this(config, defaultUrl);
        this.multiProfile = multiProfile;
    }

    public CallbackFilter(final Config config, final String defaultUrl, final boolean multiProfile, final boolean renewSession) {
        this(config, defaultUrl, multiProfile);
        this.renewSession = renewSession;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {

        assertNotNull("config", this.config);
        final J2EContext context = new J2EContext((HttpServletRequest) req, (HttpServletResponse) resp, config.getSessionStore());

        if (mustApply(context)) {
            assertNotNull("callbackLogic", this.callbackLogic);
            callbackLogic.perform(context, this.config, J2ENopHttpActionAdapter.INSTANCE, this.defaultUrl, this.saveInSession,
                    this.multiProfile, this.renewSession, this.defaultClient);
        } else {
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() { }

    public CallbackLogic<Object, J2EContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic<Object, J2EContext> callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public Boolean getRenewSession() {
        return renewSession;
    }

    public void setRenewSession(final Boolean renewSession) {
        this.renewSession = renewSession;
    }

    public Boolean getSaveInSession() {
        return saveInSession;
    }

    public void setSaveInSession(final Boolean saveInSession) {
        this.saveInSession = saveInSession;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(final String defaultClient) {
        this.defaultClient = defaultClient;
    }
}
