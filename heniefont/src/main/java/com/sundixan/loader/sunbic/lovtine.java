package com.sundixan.loader.sunbic;

import static com.sundixan.loader.BappClaass.app_Coutry_Server_Code;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.sundixan.loader.MianCallerOera;
import com.sundxin.jesdenias.R;
import com.northghost.caketube.CaketubeTransport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class lovtine {
    private AuthMethod authMethod;
    public static String selectedCountry = "";

    public static Activity activity;
    public static lovtine initiates;
    public static Boolean datra = false;

    public static UnifiedSDK unifiedSDK;

    public static String A_Token = "";
    public static String Base_Carrier = "";
    public static String Base_OAuth = "";


    public lovtine(Activity activitys) {
        activity = activitys;
    }

    public void initHydraSdk(String basecarrier) {
        Random random = new Random();
        int num = random.nextInt(2);
        try {
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .carrierId(basecarrier)
                    .build();
            unifiedSDK = UnifiedSDK.getInstance(clientInfo);

            UnifiedSDK.setLoggingLevel(Log.VERBOSE);
        } catch (Exception ignored) {
        }
    }

    public static lovtine getInstance(Activity gtactivity) {
        activity = gtactivity;
        if (initiates == null) {
            initiates = new lovtine(activity);
        }
        return initiates;
    }

    public void Initiate_call(Activity activity1, String Access_Token, String Base_auth, String Base_Carrier1) {
        activity = activity1;
        A_Token = Access_Token;
        Base_OAuth = Base_auth;
        Base_Carrier = Base_Carrier1;


        if(app_Coutry_Server_Code.equalsIgnoreCase("")){
            selectedCountry = "";
        }else{
            selectedCountry = app_Coutry_Server_Code;
        }

        try {
           initHydraSdk(Base_Carrier);
        } catch (Exception ignored) {
        }
        login(activity);

       /*
        List<Country> countryList = SharedPrefs.getCountry(activity);
        int num = new Random().nextInt(countryList.size());
        selectedCountry = countryList.get(num).getCountry(); */

    }

    public void login(Activity activity) {
        try {
            activity.runOnUiThread(() -> {
                authMethod = AuthMethod.custom(A_Token, Base_OAuth);
                unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
                    @Override
                    public void success(@NonNull User user) {
                        SharedPreferences.Editor prefsed = activity.getSharedPreferences("whatsapp_pref",
                                Context.MODE_PRIVATE).edit();
                        prefsed.putBoolean("isfirsttime", true);
                        prefsed.apply();
                        loadServers(activity);
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setTitle(activity.getString(R.string.network_error))
                                .setMessage(e.getMessage())
                                .setNegativeButton(activity.getString(R.string.ok),
                                        (dialog, id) -> {
                                            dialog.cancel();
                                            activity.onBackPressed();
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            });
        } catch (final Exception ex) {
            ex.printStackTrace();
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.network_error))
                        .setMessage(ex.getMessage())
                        .setNegativeButton(activity.getString(R.string.ok),
                                (dialog, id) -> {
                                    dialog.cancel();
                                    activity.onBackPressed();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        }
    }

    public void startMain(Activity activity) {
        if (NetworkState.isNetworkAvailable(activity)) {
            new CountDownTimer(100, 200) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println("ticking");
                }

                @Override
                public void onFinish() {

                    UnifiedSDK.getVpnState(new Callback<VPNState>() {
                        @Override
                        public void success(@NonNull VPNState state) {
                            unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                                @Override
                                public void complete() {
                                    connectToVpn(activity);
                                }

                                @Override
                                public void error(@NonNull VpnException e) {
                                    connectToVpn(activity);
                                    handleError(e);
                                }
                            });
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            handleError(e);
                        }
                    });
                }
            }.start();
        } else {
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.network_error))
                        .setMessage(activity.getString(R.string.network_error_message))
                        .setNegativeButton(activity.getString(R.string.ok),
                                (dialog, id) -> {
                                    dialog.cancel();
                                    activity.onBackPressed();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        }
    }

    public void loadServers(Activity activity) {
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                SharedPrefs.setcountry(countries.getCountries(), activity);
                startMain(activity);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                handleError(e);
                startMain(activity);
            }
        });
    }

    public void connectToVpn(Activity activity) {
        try {
            unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean isLoggedIn) {
                    if (isLoggedIn) {
                        List<String> fallbackOrder = new ArrayList<>();
                        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                        List<String> bypassDomains = new LinkedList<>();
                        bypassDomains.add("*facebook.com");
                        bypassDomains.add("*wtfismyip.com");

                        UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder().withReason(TrackingConstants.GprReasons.M_UI).withTransportFallback(fallbackOrder).withVirtualLocation(selectedCountry).withTransport(HydraTransport.TRANSPORT_ID).addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains)).build(), new CompletableCallback() {
                            @Override
                            public void complete() {
                                //btn_activation.setText("Deactive");
                                // Toast.makeText(Policy_privacy.this, getResources().getString(R.string.vpncon), Toast.LENGTH_SHORT).show();
                                changescreen(activity);
                            }

                            @Override
                            public void error(@NonNull VpnException e) {
                                //connectToVpn(activity);
                              //  Toast.makeText(activity, " Permission Denied By User", Toast.LENGTH_SHORT).show();
                                changescreen(activity);
                                //    Toast.makeText(Policy_privacy.this, getResources().getString(R.string.errorconnecting), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // Toast.makeText(Policy_privacy.this, "Restart VPN", Toast.LENGTH_SHORT).show();
                        //showMessage(getString(R.string.loginnotdone) + "");
                        login_after(activity);
                        List<String> fallbackOrder = new ArrayList<>();
                        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                        List<String> bypassDomains = new LinkedList<>();
                        bypassDomains.add("*facebook.com");
                        bypassDomains.add("*wtfismyip.com");

                        UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder().withReason(TrackingConstants.GprReasons.M_UI).withTransportFallback(fallbackOrder).withVirtualLocation(selectedCountry).withTransport(HydraTransport.TRANSPORT_ID).addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains)).build(), new CompletableCallback() {
                            @Override
                            public void complete() {
                                Toast.makeText(activity, activity.getResources().getString(R.string.vpncon), Toast.LENGTH_SHORT).show();
                                changescreen(activity);
                            }

                            @Override
                            public void error(@NonNull VpnException e) {
                                connectToVpn(activity);
                                Toast.makeText(activity, activity.getResources().getString(R.string.errorconnecting), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                    handleError(e);
                    changescreen(activity);
                }
            });
        } catch (Exception ignored) {
        }
    }

    public void changescreen(Activity activity) {
        MianCallerOera.getInstance(activity).Allloadeddarts();

    }


    public void login_after(Activity activity) {
        try {
            ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getString(R.string.pleasewait) + "");
            progressDialog.show();
            activity.runOnUiThread(() -> {
                AuthMethod authMethod = AuthMethod.anonymous();
                unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
                    @Override
                    public void success(@NonNull User user) {
                        progressDialog.dismiss();
                        Toast.makeText(activity, activity.getResources().getString(R.string.logindone), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                        handleError(e);
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(activity.getString(R.string.network_error)).setMessage(e.getMessage()).setNegativeButton(activity.getString(R.string.ok), (dialog, id) -> {
                            dialog.cancel();
                            activity.onBackPressed();
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            });

        } catch (final Exception ex) {
            ex.printStackTrace();
            activity.runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.network_error)).setMessage(ex.getMessage()).setNegativeButton(activity.getString(R.string.ok), (dialog, id) -> {
                    dialog.cancel();
                    activity.onBackPressed();
                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        }
    }

   /* public void checkVpnLoginAtStart(Activity activity) {
        unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
                if (!isLoggedIn) {
                    login_after(activity);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                handleError(e);
            }
        });
    }*/

    public void handleError(Throwable e) {
    }

}
