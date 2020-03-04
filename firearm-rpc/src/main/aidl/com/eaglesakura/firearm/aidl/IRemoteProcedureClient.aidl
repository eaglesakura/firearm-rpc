// IRemoteProcedureClient.aidl
package com.eaglesakura.firearm.aidl;

import android.os.Bundle;

interface IRemoteProcedureClient {

    /**
     * an AIDL Server post data to client.
     */
    Bundle requestFromService(in Bundle arguments);
}
