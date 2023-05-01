package com.coopnfc.example;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CoopNFC")
public class CoopNFCPlugin extends Plugin {

    private CoopNFC implementation;

    @Override
    public void load() {
        implementation = new CoopNFC(getContext());
    }


    @PluginMethod
    public void waitNFC(PluginCall call) {
        String res = implementation.CallNfc();
        JSObject ret = new JSObject();
        ret.put("result", res)
        call.resolve(ret);
    }


    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", "Default Web Implementation");
        call.resolve(ret);
    }

}



