/*  Copyright 2012 Jonas Schürmann ©

    My Website: mazechazer.jimdo.com

    This file is part of  the WoT Tank Quiz.

    The WoT Tank Quiz is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The WoT Tank Quiz is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the WoT Tank Quiz.  If not, see <http://www.gnu.org/licenses/>. */

package mazechazer.android.wottankquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParserException;

public class ActivityQuizScreen extends Activity {
	int score = 0, secondsLeft = 60;
    ImageView tankImage;
    TextView textPoints, textTime;
    Bundle b;
    ArrayList<Tank> tankList = new ArrayList<Tank>();
	ArrayList<Button> answerButtons = new ArrayList<Button>();
	Dialog highscoreDialog;
	int level, answer, givenAnswer;
	String name;
	boolean buttonsActivated = true;
	CountDownTimer countdown;
	boolean paused = false;
	boolean allowPauseGame = true;
	int lastHighscore;
	String levelString;
	boolean newHighscoreAlreadySaid = false;
	boolean gameStopped = false;
	ArrayList<Tank> unusedTanks;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
	    b = this.getIntent().getExtras();
	    if (b!=null){
	    	level = b.getInt("level");
	    }
	    tankImage = (ImageView) findViewById(R.id.imageView1);
	    textPoints = (TextView) findViewById(R.id.textViewPoints);
	    textTime = (TextView) findViewById(R.id.textViewTime);
		answerButtons.add((Button) findViewById(R.id.button1));
		answerButtons.add((Button) findViewById(R.id.button2));
		answerButtons.add((Button) findViewById(R.id.button3));
		answerButtons.add((Button) findViewById(R.id.button4));
	    textPoints.setText(getResources().getText(R.string.Points) + ": " + score);
	    textTime.setText(getResources().getText(R.string.Time) + ": " + secondsLeft);
	    switch (level){
		case 1:
			levelString = "Easy";
			break;
		case 2:
			levelString = "Medium";
			break;
		case 3:
			levelString = "Hard";
			break;
	    }
	    lastHighscore = getSharedPreferences("highscore", MODE_PRIVATE).getInt(levelString + "Score" + "1", -10000);
	    addTanks();
	    chooseTank();
	    startCountdownAgain();
	}
	public void startCountdownAgain(){
		countdown = new CountDownTimer(secondsLeft * 1000, 1000) {

	        public void onTick(long millisUntilFinished) {
	        	secondsLeft -= 1;
	            textTime.setText(getResources().getText(R.string.Time) + ": " + Integer.toString(secondsLeft));
	        }

	        public void onFinish() {
	        	gameStopped = true;
	        	allowPauseGame = false;
	            highscoreDialog = new Dialog(ActivityQuizScreen.this);
	            highscoreDialog.setContentView(R.layout.hightscoredialog);
	            highscoreDialog.setCancelable(false);
	            highscoreDialog.setTitle(R.string.Highscore);
	            highscoreDialog.show();
	            CharSequence statement;
	            if (score < 0){
	            	statement = getResources().getText(R.string.StatementLessThanZero);
	            } else if (score <= 10){
	            	statement = getResources().getText(R.string.StatementZeroToTen);
	            } else if (score <= 20){
	            	statement = getResources().getText(R.string.StatementElevenToTwenty);
	            } else if (score <= 30){
	            	statement = getResources().getText(R.string.StatementTwentyoneToThirty);
	            } else {
	            	statement = getResources().getText(R.string.StatementThirtyoneAndMore);
	            }
	            ((TextView) highscoreDialog.findViewById(R.id.textViewHighscore)).setText(statement + "\n" + getResources().getText(R.string.Points) + ": " + Integer.toString(score) + "\n" + getResources().getText(R.string.PleaseEnterYourName));
	            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
	            ((EditText) highscoreDialog.findViewById(R.id.editText1)).setText(prefs.getString("lastUsedName", ""));
	            ((EditText) highscoreDialog.findViewById(R.id.editText1)).addTextChangedListener(new TextWatcher(){

					public void afterTextChanged(Editable arg0) {
					}
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					}
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						if (arg0.toString().equals("")){
							((Button) highscoreDialog.findViewById(R.id.buttonHighscore)).setEnabled(false);
						} else {
							((Button) highscoreDialog.findViewById(R.id.buttonHighscore)).setEnabled(true);
						}
					}
	            });
	            Button buttonHighscore = (Button) highscoreDialog.findViewById(R.id.buttonHighscore);
	            buttonHighscore.setEnabled(! ((EditText) highscoreDialog.findViewById(R.id.editText1)).getText().toString().equals(""));
	            buttonHighscore.setOnClickListener(new OnClickListener() {
	                    public void onClick(View v) {
	                    	name = ((EditText) highscoreDialog.findViewById(R.id.editText1)).getText().toString();
	                    	SharedPreferences saving = getSharedPreferences("highscore", MODE_PRIVATE);
	                		SharedPreferences.Editor editor = saving.edit();
	            			if (score > saving.getInt(levelString + "Score" + Integer.toString(10), -10000)){
	            				int position = 1;
	            				for (int i = 9; i >= 1; i--){
	            					if (score <= saving.getInt(levelString +"Score" + Integer.toString(i), -10000)){
	            						position = i + 1;
	            						break;
	            					}
	            				}
	            				for (int i = 10; i >= position + 1; i--){
	            					editor.putString(levelString + "Name" + Integer.toString(i), saving.getString(levelString + "Name" + Integer.toString(i - 1), ""));
	            					editor.putInt(levelString + "Score" + Integer.toString(i), saving.getInt(levelString + "Score" + Integer.toString(i - 1), 0));
	            				}
	            				editor.putString(levelString + "Name" + Integer.toString(position), name);
	            				editor.putInt(levelString + "Score" + Integer.toString(position), score);
	            				editor.commit();
	            			}
	            			SharedPreferences.Editor localEditor = getPreferences(MODE_PRIVATE).edit();
	            			localEditor.putString("lastUsedName", name);
	            			localEditor.commit();
	            			highscoreDialog.dismiss();
	            			ActivityQuizScreen.this.finish();
	                    }
	                });
	        }	        
	     };
	     countdown.start();
	}
	@Override
	public void onBackPressed() {
		pause();
	}
	@Override
	public void onPause(){
		super.onPause();
		pause();
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		pause();
		return true;
	}
	protected void pause(){
		if (allowPauseGame && ! paused){
			paused = true;
			countdown.cancel();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.GamePaused);
			builder.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   startCountdownAgain();
		        	   paused = false;
		           }
		       });
			builder.setNegativeButton(R.string.BackToMenu, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					startCountdownAgain();
		        	paused = false;
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	@SuppressWarnings("unchecked")
	private void addTanks(){
            XmlResourceParser tanks = getResources().getXml(R.xml.tanks);

            int eventType = -1;
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                    if (eventType == XmlResourceParser.START_TAG) {
                            String strName = tanks.getName();
                            if (strName.equals("tank")) {

                                    // No getters, no setters - so why not snake?
                                    Tank tank = new Tank(
                                                    tanks.getAttributeValue(null, "name"),
                                                    getResources().getIdentifier(
                                                                    tanks.getAttributeValue(null, "pic"),
                                                                    "drawable", getPackageName()), Country
                                                                    .valueOf(tanks.getAttributeValue(null,
                                                                                    "country")), TankClass
                                                                    .valueOf(tanks.getAttributeValue(null,
                                                                                    "class")));
                                    
                                    tankList.add(tank);

                            }
                    }

                    try {
                            eventType = tanks.next();
                    } catch (IOException ioException) {
                            Toast.makeText(this, "Error i/o", Toast.LENGTH_LONG).show();
                    } catch (XmlPullParserException xmlPullParserException) {
                            Toast.makeText(this, "Error parse xml", Toast.LENGTH_LONG)
                                            .show();
                    }
            }
            
            /*
            tankList.add(new Tank("Leichttraktor", R.drawable.germany_ltraktor, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw 35 (t)", R.drawable.germany_pz35t, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw II", R.drawable.germany_pzii, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw 38H735 (f)", R.drawable.germany_h39_captured, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw 38 (t)", R.drawable.germany_pz38t, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw III Ausf. A", R.drawable.germany_pziii_a, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw II Luchs", R.drawable.germany_pzii_luchs, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw II Ausf. J", R.drawable.germany_pzii_j, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-15", R.drawable.germany_t_15, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("VK 1602 Leopard", R.drawable.germany_vk1602, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw 38 nA", R.drawable.germany_pz38_na, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("VK 2801", R.drawable.germany_vk2801, Country.GERMANY, TankClass.LIGHTTANK));
	    tankList.add(new Tank("PzKpfw S35 739 (f)", R.drawable.germany_s35_captured, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw III", R.drawable.germany_pziii, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw IV", R.drawable.germany_pziv, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw III/IV", R.drawable.germany_pziii_iv, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-25", R.drawable.germany_t_25, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw IV hydrostat.", R.drawable.germany_pziv_hydro, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("VK 3601 (H)", R.drawable.germany_vk3601h, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("VK 3001 (H)", R.drawable.germany_vk3001h, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("VK 3001 (P)", R.drawable.germany_vk3001p, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw V-IV", R.drawable.germany_pzv_pziv, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw V-IV Alpha", R.drawable.germany_pzv_pziv_ausf_alfa, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw IV Schmalturm", R.drawable.germany_pziv_schmalturm, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw V Panther", R.drawable.germany_pzv, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("VK 3002 (DB)", R.drawable.germany_vk3002db, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Panther-M10", R.drawable.germany_panther_m10, Country.GERMANY, TankClass.MEDIUMTANK));
	    
	    tankList.add(new Tank("Panther II", R.drawable.germany_panther_ii, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("E-50", R.drawable.germany_e_50, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("E-50 Ausf.M", R.drawable.germany_e50_ausf_m, Country.GERMANY, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("PzKpfw B2 740 (f)", R.drawable.germany_b_1bis_captured, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("PzKpfw VI Tiger", R.drawable.germany_pzvi, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("PzKpfw VI Tiger (P)", R.drawable.germany_pzvi_tiger_p, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("PzKpfw VIB Tiger II", R.drawable.germany_pzvib_tiger_ii, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("VK 4502 (P) Ausf. A", R.drawable.germany_vk4502a, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Löwe", R.drawable.germany_lowe, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("VK 4502 (P) Ausf. B", R.drawable.germany_vk4502p, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("E-75", R.drawable.germany_e_75, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Maus", R.drawable.germany_maus, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("E-100", R.drawable.germany_e_100, Country.GERMANY, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Panzerjäger I", R.drawable.germany_panzerjager_i, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Marder II", R.drawable.germany_g20_marder_ii, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Hetzer", R.drawable.germany_hetzer, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("StuG III", R.drawable.germany_stugiii, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("JagdPz IV", R.drawable.germany_jagdpziv, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Dicker Max", R.drawable.germany_dickermax, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Jagdpanther", R.drawable.germany_jagdpanther, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Ferdinand", R.drawable.germany_ferdinand, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("JagdPanther II", R.drawable.germany_jagdpantherii, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Jagdtiger 8,8 cm PaK 43", R.drawable.germany_jagdtiger_sdkfz_185, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Jagdtiger", R.drawable.germany_jagdtiger, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("JagdPz E-100", R.drawable.germany_jagdpz_e100, Country.GERMANY, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Sturmpanzer I Bison", R.drawable.germany_bison_i, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("Sturmpanzer II", R.drawable.germany_sturmpanzer_ii, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("Wespe", R.drawable.germany_wespe, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("Grille", R.drawable.germany_grille, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("Hummel", R.drawable.germany_hummel, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("GW Panther", R.drawable.germany_g_panther, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("GW Tiger", R.drawable.germany_g_tiger, Country.GERMANY, TankClass.SPG));
	    tankList.add(new Tank("GW Typ E", R.drawable.germany_g_e, Country.GERMANY, TankClass.SPG));
	    
	    tankList.add(new Tank("MS-1", R.drawable.ussr_ms_1, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("BT-2", R.drawable.ussr_bt_2, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-26", R.drawable.ussr_t_26, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("MkVII Tetrach", R.drawable.ussr_tetrarch_ll, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("BT-7", R.drawable.ussr_bt_7, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-46", R.drawable.ussr_t_46, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("BT-SV", R.drawable.ussr_bt_sv, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M3 Stuart", R.drawable.ussr_m3_stuart_ll, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-127", R.drawable.ussr_t_127, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("A-20", R.drawable.ussr_a_20, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-50", R.drawable.ussr_t_50, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Valentine", R.drawable.ussr_valentine_ll, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-50-2", R.drawable.ussr_t_50_2, Country.USSR, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T-28", R.drawable.ussr_t_28, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("A-32", R.drawable.ussr_a_32, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-34", R.drawable.ussr_t_34, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Matilda", R.drawable.ussr_matilda_ii_ll, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-34-85", R.drawable.ussr_t_34_85, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-43", R.drawable.ussr_t_43, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("KV-13", R.drawable.ussr_kv_13, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-44", R.drawable.ussr_t_44, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-54", R.drawable.ussr_t_54, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T-62A", R.drawable.ussr_t62a, Country.USSR, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("KV", R.drawable.ussr_kv, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-1", R.drawable.ussr_kv1, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-220 Beta-Test", R.drawable.ussr_kv_220, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Churchill", R.drawable.ussr_churchill_ll, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-220", R.drawable.ussr_kv_220_action, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-1S", R.drawable.ussr_kv_1s, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-2", R.drawable.ussr_kv2, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T-150", R.drawable.ussr_t150, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-3", R.drawable.ussr_kv_3, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS", R.drawable.ussr_is, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS-3", R.drawable.ussr_is_3, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS-6", R.drawable.ussr_object252, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-4", R.drawable.ussr_kv4, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("KV-5", R.drawable.ussr_kv_5, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("ST-I", R.drawable.ussr_st_i, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS-8", R.drawable.ussr_is8, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS-4", R.drawable.ussr_is_4, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("IS-7", R.drawable.ussr_is_7, Country.USSR, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AT-1", R.drawable.ussr_at_1, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-76", R.drawable.ussr_su_76, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-85B",R.drawable.ussr_gaz_74b, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-85", R.drawable.ussr_su_85, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-85I", R.drawable.ussr_su_85i, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-100", R.drawable.ussr_su_100, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-152", R.drawable.ussr_su_152, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-100M1", R.drawable.ussr_su100m1, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-122-44", R.drawable.ussr_su122_44, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("ISU-152", R.drawable.ussr_isu_152, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-101", R.drawable.ussr_su_101, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Objekt 704", R.drawable.ussr_object_704, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-122-54", R.drawable.ussr_su122_54, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Object 268", R.drawable.ussr_object268, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Object 263", R.drawable.ussr_object263, Country.USSR, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("SU-18", R.drawable.ussr_su_18, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("SU-26", R.drawable.ussr_su_26, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("SU-5", R.drawable.ussr_su_5, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("SU-8", R.drawable.ussr_su_8, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("S-51", R.drawable.ussr_s_51, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("SU-14", R.drawable.ussr_su_14, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("Objekt 212", R.drawable.ussr_object_212, Country.USSR, TankClass.SPG));
	    tankList.add(new Tank("Objekt 261", R.drawable.ussr_object_261, Country.USSR, TankClass.SPG));
	    
	    tankList.add(new Tank("T1 Cunningham", R.drawable.usa_t1_cunningham, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M2 Light Tank", R.drawable.usa_m2_lt, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T2 Light", R.drawable.usa_t2_lt, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M3 Stuart", R.drawable.usa_m3_stuart, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("MTLS-1G14", R.drawable.usa_mtls_1g14, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M22 Locust", R.drawable.usa_m22_locust, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M5 Stuart", R.drawable.usa_m5_stuart, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("M24 Chaffee", R.drawable.usa_m24_chaffee, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T21", R.drawable.usa_t21, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T71", R.drawable.usa_t71, Country.USA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("T2 Medium Tank", R.drawable.usa_t2_med, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M2 Medium Tank", R.drawable.usa_m2_med, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M3 Lee", R.drawable.usa_m3_grant, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M4 Sherman", R.drawable.usa_m4_sherman, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M7", R.drawable.usa_m7_med, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Ram-II", R.drawable.usa_ram_ii, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M4A2E4", R.drawable.usa_m4a2e4, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M4A3E8 Sherman", R.drawable.usa_m4a3e8_sherman, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M4A3E2", R.drawable.usa_sherman_jumbo, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T20", R.drawable.usa_t20, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M26 Pershing", R.drawable.usa_pershing, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T26E4 SuperPershing", R.drawable.usa_t26_e4_superpershing, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T69", R.drawable.usa_t69, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M46 Patton", R.drawable.usa_m46_patton, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T54E1", R.drawable.usa_t54e1, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("M48A1", R.drawable.usa_m48a1, Country.USA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("T14", R.drawable.usa_t14, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T1 heavy", R.drawable.usa_t1_hvy, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("M6", R.drawable.usa_m6, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T29", R.drawable.usa_t29, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T34", R.drawable.usa_t34_hvy, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T32", R.drawable.usa_t32, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("M6A2E1", R.drawable.usa_m6a2e1, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("M103", R.drawable.usa_m103, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T110E5", R.drawable.usa_t110, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T57 Heavy Tank", R.drawable.usa_t57_58, Country.USA, TankClass.HEAVYTANK));
	    tankList.add(new Tank("T30", R.drawable.usa_t30, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T18", R.drawable.usa_t18, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T82", R.drawable.usa_t82, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T40", R.drawable.usa_t40, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("M8A1", R.drawable.usa_m8a1, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("M10 Wolverine", R.drawable.usa_m10_wolverine, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T49", R.drawable.usa_t49, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("M36 Slugger", R.drawable.usa_m36_slagger, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("M18 Hellcat", R.drawable.usa_m18_hellcat, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T25 AT", R.drawable.usa_t25_at, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T25/2", R.drawable.usa_t25_2, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T28", R.drawable.usa_t28, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T28-Prototyp", R.drawable.usa_t28_prototype, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T30", R.drawable.usa_t30, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T95", R.drawable.usa_t95, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T110E4", R.drawable.usa_t110e4, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T110E3", R.drawable.usa_t110e3, Country.USA, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("T57", R.drawable.usa_t57, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("M37", R.drawable.usa_m37, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("M7 Priest", R.drawable.usa_m7_priest, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("M41", R.drawable.usa_m41, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("M12", R.drawable.usa_m12, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("M40/M43", R.drawable.usa_m40m43, Country.USA, TankClass.SPG));
	    tankList.add(new Tank("T92", R.drawable.usa_t92, Country.USA, TankClass.SPG));
	    	    
	    tankList.add(new Tank("RenaultFT", R.drawable.france_renaultft, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Hotchkiss H35", R.drawable.france_hotchkiss_h35, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("D1", R.drawable.france_d1, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("AMX 38", R.drawable.france_amx38, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("AMX 40" , R.drawable.france_amx40, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("ELC AMX" , R.drawable.france_elc_amx, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("AMX 12t", R.drawable.france_amx_12t, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("AMX 13 75", R.drawable.france_amx_13_75, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("AMX 13 90", R.drawable.france_amx_13_90, Country.FRANCE, TankClass.LIGHTTANK));
	    tankList.add(new Tank("D2", R.drawable.france_d2, Country.FRANCE, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Lorraine 40 t", R.drawable.france_lorraine40t, Country.FRANCE, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Bat Chatillon 25 t", R.drawable.france_bat_chatillon25t, Country.FRANCE, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("B1", R.drawable.france_b1, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("BDR G1B", R.drawable.france_bdr_g1b, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("ARL 44", R.drawable.france_arl_44, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AMX M4(1945)", R.drawable.france_amx_m4_1945, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AMX 50 100", R.drawable.france_amx_50_100, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("FCM 50 t", R.drawable.france_fcm_50t, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AMX 50 120", R.drawable.france_amx_50_120, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AMX 50B", R.drawable.france_f10_amx_50b, Country.FRANCE, TankClass.HEAVYTANK));
	    tankList.add(new Tank("RenaultFT AC", R.drawable.france_renaultft_ac, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("FCM36 Pak40", R.drawable.france_fcm_36pak40, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Renault UE 57", R.drawable.france_renaultue57, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("Somua SAu-40", R.drawable.france_somua_sau_40, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("S-35 CA", R.drawable.france_s_35ca, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("ARL V39", R.drawable.france_arl_v39, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("AMX AC Mle.1946", R.drawable.france_amx_ac_mle1946, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("AMX AC Mle. 1948", R.drawable.france_amx_ac_mle1948, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("AMX 50 Foch", R.drawable.france_amx50_foch, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("AMX-50 Foch (155)", R.drawable.france_amx_50fosh_155, Country.FRANCE, TankClass.TANKDESTROYER));
	    tankList.add(new Tank("RenaultBS", R.drawable.france_renaultbs, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("Lorraine39 L AM", R.drawable.france_lorraine39_l_am, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("105 leFH18B2", R.drawable.france__105_lefh18b2, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("AMX 105AM", R.drawable.france_amx_105am, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("AMX-13 F3 AM", R.drawable.france_amx_13f3am, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("Lorraine155 50", R.drawable.france_lorraine155_50, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("Lorraine155 51", R.drawable.france_lorraine155_51, Country.FRANCE, TankClass.SPG));
	    tankList.add(new Tank("Bat Chatillon 155", R.drawable.france_bat_chatillon155, Country.FRANCE, TankClass.SPG));
	    
	    tankList.add(new Tank("Type 62", R.drawable.china_ch02_type62, Country.CHINA, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Typ 59", R.drawable.china_ch01_type59, Country.CHINA, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("WZ-111", R.drawable.china_ch03_wz_111, Country.CHINA, TankClass.HEAVYTANK));
	    
	    tankList.add(new Tank("Cruiser MK. I", R.drawable.uk_gb03_cruiser_mk_i, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("A13 Mk. I Cr. Tank Mk. III", R.drawable.uk_gb58_cruiser_mk_iii, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("A10 Cruiser Mk. II", R.drawable.uk_gb69_cruiser_mk_ii, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("A13 Mk. II Cr. Tank Mk. IV", R.drawable.uk_gb59_cruiser_mk_iv, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Valentine Mk.III", R.drawable.uk_gb04_valentine, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("A13 Covenanter", R.drawable.uk_gb60_covenanter, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Crusader", R.drawable.uk_gb20_crusader, Country.UK, TankClass.LIGHTTANK));
	    tankList.add(new Tank("Medium Mark I", R.drawable.uk_gb01_medium_mark_i, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Vickers Medium Mk. II", R.drawable.uk_gb05_vickers_medium_mk_ii, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Vickers Medium Mk. III", R.drawable.uk_gb06_vickers_medium_mk_iii, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Matilda", R.drawable.uk_gb07_matilda, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Matilda Black Prince", R.drawable.uk_gb68_matilda_black_prince, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Cromwell", R.drawable.uk_gb21_cromwell, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Comet", R.drawable.uk_gb22_comet, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Centurion Mk. I", R.drawable.uk_gb23_centurion, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Centurion Mk. 7/1", R.drawable.uk_gb24_centurion_mk3, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("FV4202(105)", R.drawable.uk_gb70_fv4202_105, Country.UK, TankClass.MEDIUMTANK));
	    tankList.add(new Tank("Churchill I", R.drawable.uk_gb08_churchill_i, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Churchill VII", R.drawable.uk_gb09_churchill_vii, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("TOG II*", R.drawable.uk_gb63_tog_ii, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Black Prince", R.drawable.uk_gb10_black_prince, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Caernarvon", R.drawable.uk_gb11_caernarvon, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("Conqueror", R.drawable.uk_gb12_conqueror, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("FV215b", R.drawable.uk_gb13_fv215b, Country.UK, TankClass.HEAVYTANK));
	    tankList.add(new Tank("AT-15A", R.drawable.uk_gb71_at_15a, Country.UK, TankClass.TANKDESTROYER));
	    */
	    unusedTanks = (ArrayList<Tank>) tankList.clone();

	}
	@SuppressWarnings("unchecked")
	private void chooseTank(){
		if (unusedTanks.size() == 0){
			unusedTanks = (ArrayList<Tank>) tankList.clone();
		}
		ArrayList<Tank> chooseList = (ArrayList<Tank>) unusedTanks.clone();
		ArrayList<Tank> alternateChooseList = (ArrayList<Tank>) unusedTanks.clone();
		ArrayList<Button> chooseAnswerButtons = (ArrayList<Button>) answerButtons.clone();
		Tank choosenTank1 = chooseList.get((int) Math.round(Math.random() * chooseList.size() - 0.5));
		Log.d("wot", choosenTank1.name);
		tankImage.setImageResource(choosenTank1.resID);
		chooseList.remove(choosenTank1);
		alternateChooseList.remove(choosenTank1);
		unusedTanks.remove(choosenTank1);
		answer = (int) Math.round(Math.random() * chooseAnswerButtons.size() + 0.5);
		chooseAnswerButtons.get(answer - 1).setText(choosenTank1.name);
		chooseAnswerButtons.remove(answer - 1);
		boolean chooseListBecameEmpty = false;
		if (level == 1){
			for (int i = chooseList.size() - 1; i >= 0; i -= 1){
				if (chooseList.get(i).country == choosenTank1.country){
					chooseList.remove(i);
				}
			}
		} else if (level == 2){
			for (int i = chooseList.size() - 1; i >= 0; i -= 1){
				if ( (! (chooseList.get(i).country == choosenTank1.country) ) || chooseList.get(i).tankClass == choosenTank1.tankClass){
					chooseList.remove(i);
				}
			}
		} else if (level == 3){
			for (int i = chooseList.size() - 1; i >= 0; i -= 1){
				if ((! (chooseList.get(i).tankClass == choosenTank1.tankClass) ) || !(chooseList.get(i).country == choosenTank1.country)){
					chooseList.remove(i);
				}
			}
		}
		if (chooseList.size() == 0){
			chooseListBecameEmpty = true;
		}
		for (int j = 1; j <= 3; j++){
			if (! chooseListBecameEmpty){
				Tank choosenTank = chooseList.get((int) Math.round(Math.random() * chooseList.size() - 0.5));
				chooseList.remove(choosenTank);
				alternateChooseList.remove(choosenTank);
				int alternateAnswerButton = (int) Math.round(Math.random() * chooseAnswerButtons.size() + 0.5);
				chooseAnswerButtons.get(alternateAnswerButton - 1).setText(choosenTank.name);
				chooseAnswerButtons.remove(alternateAnswerButton - 1);
				if (level == 1){
					for (int i = chooseList.size() - 1; i >= 0; i -= 1){
						if (chooseList.get(i).country == choosenTank.country){
							chooseList.remove(i);
						}
					}
				} else if (level == 2){
					for (int i = chooseList.size() - 1; i >= 0; i -= 1){
						if ( (! (chooseList.get(i).country == choosenTank.country) ) || chooseList.get(i).tankClass == choosenTank.tankClass){
							chooseList.remove(i);
						}
					}
				} else if (level == 3){
					for (int i = chooseList.size() - 1; i >= 0; i -= 1){
						if ((! (chooseList.get(i).tankClass == choosenTank.tankClass) ) || !(chooseList.get(i).country == choosenTank.country)){
							chooseList.remove(i);
						}
					}
				}
				if (chooseList.size() == 0){
					chooseListBecameEmpty = true;
				}
			} else {
				Tank choosenTank = alternateChooseList.get((int) Math.round(Math.random() * alternateChooseList.size() - 0.5));
				alternateChooseList.remove(choosenTank);
				int alternateAnswerButton = (int) Math.round(Math.random() * chooseAnswerButtons.size() + 0.5);
				chooseAnswerButtons.get(alternateAnswerButton - 1).setText(choosenTank.name);
				chooseAnswerButtons.remove(alternateAnswerButton - 1);
			}
		}
	}
	public void buttonAnswerClick(View view){
		if (buttonsActivated && ! gameStopped){
			buttonsActivated = false;
			Button clickedButton = (Button) view;
			switch (clickedButton.getId()){
			case R.id.button1:
				givenAnswer = 1;
				break;
			case R.id.button2:
				givenAnswer = 2;
				break;
			case R.id.button3:
				givenAnswer = 3;
				break;
			case R.id.button4:
				givenAnswer = 4;
				break;
			}
			if (givenAnswer == answer){
				score += 1;
				textPoints.setText(getResources().getText(R.string.Points) + ": " + Integer.toString(score));
				answerButtons.get(answer - 1).setBackgroundResource(R.drawable.buttongreen);
				Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			              answerButtons.get(answer - 1).setBackgroundResource(R.drawable.buttonstandard);
			              chooseTank();
			              buttonsActivated = true;
			         } 
			    }, 200);
			    				
			} else {
				
				score -= 1;
				textPoints.setText(getResources().getText(R.string.Points) + ": " + Integer.toString(score));
				answerButtons.get(givenAnswer - 1).setBackgroundResource(R.drawable.buttonred);
				answerButtons.get(answer - 1).setBackgroundResource(R.drawable.buttongreen);
				Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() {
			        	 answerButtons.get(givenAnswer - 1).setBackgroundResource(R.drawable.buttonstandard);
			        	 answerButtons.get(answer - 1).setBackgroundResource(R.drawable.buttonstandard);
			        	 chooseTank();
			        	 buttonsActivated = true;
			         } 
			    }, 1000); 
			}
			if (! newHighscoreAlreadySaid){
				if (score > lastHighscore){
					Toast toast = Toast.makeText(getApplicationContext(), R.string.NewHighscore, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
					newHighscoreAlreadySaid = true;
				}
			}
		}
	}
}