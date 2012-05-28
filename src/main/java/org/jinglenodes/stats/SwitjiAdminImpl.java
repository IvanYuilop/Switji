package org.jinglenodes.stats;

import org.jinglenodes.component.SIPGatewayApplication;
import org.jinglenodes.credit.CallKiller;
import org.jinglenodes.jingle.reason.Reason;
import org.jinglenodes.jingle.reason.ReasonType;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 2:51 PM
 */
public class SwitjiAdminImpl implements SwitjiAdmin {

    private CallKiller callKiller;
    private SIPGatewayApplication sipApplication;

    @Override
    public boolean killSession(String sessionId) {
        return callKiller.immediateKill(sessionId, new Reason( new ReasonType(ReasonType.Name.general_error)));
    }

    @Override
    public int killAll() {
        return callKiller.killAll(new Reason(new ReasonType(ReasonType.Name.general_error)));
    }

    public CallKiller getCallKiller() {
        return callKiller;
    }

    public void setCallKiller(CallKiller callKiller) {
        this.callKiller = callKiller;
    }

    public SIPGatewayApplication getSipApplication() {
        return sipApplication;
    }

    public void setSipApplication(SIPGatewayApplication sipApplication) {
        this.sipApplication = sipApplication;
    }
}
