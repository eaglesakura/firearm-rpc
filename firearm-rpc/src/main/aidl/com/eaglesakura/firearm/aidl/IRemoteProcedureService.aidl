// IRemoteProcedureService.aidl
package com.eaglesakura.firearm.aidl;

import android.os.Bundle;
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient;

interface IRemoteProcedureService {
    /**
     * make a new session.
     */
    Bundle register(IRemoteProcedureClient client, in Bundle options);

    /**
     * an AIDL Client post data to server.
     */
    Bundle requestFromClient(String clientId, in Bundle arguments);

    /**
     * delete a session.
     */
    Bundle unregister(String clientId);
}
