package com.textaura.social;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "textaura";
    private static final String KEY_LANG = "lang";
    private static final String KEY_DRAFT = "draft";
    private static final int[] PALETTE = {0xFFF5F3FF,0xFFFDF2F8,0xFFEFF6FF,0xFFF0FDFA,0xFFFFFBEB,0xFFFFF1F2};
    private EditText editor;
    private TextView preview;
    private TextView count;
    private final Deque<String> undo = new ArrayDeque<>();
    private boolean internal;
    private String before="";
    private int langIndex;

    @Override protected void attachBaseContext(Context base){
        String code=base.getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_LANG,null);
        if(code==null){ String d=Locale.getDefault().getLanguage(); code=(d.equals("fr")||d.equals("en")||d.equals("es")||d.equals("de")||d.equals("ar"))?d:"fr"; }
        Locale locale=Locale.forLanguageTag(code); Locale.setDefault(locale);
        Configuration c=new Configuration(base.getResources().getConfiguration()); c.setLocale(locale); c.setLayoutDirection(locale);
        super.attachBaseContext(base.createConfigurationContext(c));
    }

    @Override protected void onCreate(Bundle b){
        super.onCreate(b); langIndex=indexOf(currentCode());
        getWindow().setStatusBarColor(Color.TRANSPARENT); getWindow().setNavigationBarColor(0xFFF4F1FF);
        setContentView(build()); restore(getIntent());
    }
    @Override protected void onNewIntent(Intent i){ super.onNewIntent(i); setIntent(i); restore(i); }
    @Override protected void onPause(){ super.onPause(); getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString(KEY_DRAFT,editor.getText().toString()).apply(); }

    private View build(){
        FrameLayout root=new FrameLayout(this); root.setBackground(gradient(0xFFF9F7FF,0xFFECFAFF,0));
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        LinearLayout body=col(); body.setPadding(dp(16),dp(20),dp(16),dp(32)); scroll.addView(body);
        body.addView(header()); gap(body,14); body.addView(editorCard()); gap(body,14); body.addView(stylesCard()); gap(body,14); body.addView(previewCard()); gap(body,14); body.addView(shareCard()); gap(body,12);
        TextView p=text(t("privacy"),12,0xFF667085); p.setGravity(Gravity.CENTER); body.addView(p);
        root.addView(scroll,new FrameLayout.LayoutParams(-1,-1)); return root;
    }
    private View header(){
        LinearLayout box=col(); box.setPadding(dp(18),dp(18),dp(18),dp(18)); box.setBackground(gradient(0xFF7C3AED,0xFFEC4899,24)); box.setElevation(dp(6));
        LinearLayout row=row(); row.setGravity(Gravity.CENTER_VERTICAL);
        TextView logo=text("Aa✦",22,0xFF7C3AED); logo.setTypeface(Typeface.DEFAULT_BOLD); logo.setGravity(Gravity.CENTER); logo.setBackground(round(Color.WHITE,18,0,0)); row.addView(logo,new LinearLayout.LayoutParams(dp(64),dp(52)));
        LinearLayout titles=col(); LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1); tp.setMargins(dp(12),0,dp(8),0); row.addView(titles,tp);
        TextView n=text("TextAura",26,Color.WHITE); n.setTypeface(Typeface.DEFAULT_BOLD); titles.addView(n); titles.addView(text(t("tag"),14,0xEEFFFFFF));
        Button l=button(currentCode().toUpperCase(Locale.ROOT),0x30FFFFFF,Color.WHITE,13); l.setOnClickListener(v->languages()); row.addView(l,new LinearLayout.LayoutParams(dp(60),dp(44)));
        box.addView(row); return box;
    }
    private View editorCard(){
        LinearLayout c=card(); LinearLayout r=row(); r.setGravity(Gravity.CENTER_VERTICAL); TextView title=title(t("your_text")); r.addView(title,new LinearLayout.LayoutParams(0,-2,1)); count=text("0 "+t("chars"),12,0xFF667085); r.addView(count); c.addView(r);
        TextView tip=text(t("tip"),12,0xFF667085); tip.setPadding(0,dp(6),0,dp(10)); c.addView(tip);
        editor=new EditText(this); editor.setHint(t("hint")); editor.setTextSize(18); editor.setTextColor(0xFF1B1F3B); editor.setHintTextColor(0xFFA5ABC2); editor.setGravity(Gravity.TOP|Gravity.START); editor.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG); editor.setMinHeight(dp(190)); editor.setPadding(dp(14),dp(14),dp(14),dp(14)); editor.setBackground(round(0xFFFAFAFF,16,0xFFE1E1F0,1)); c.addView(editor,new LinearLayout.LayoutParams(-1,-2));
        editor.addTextChangedListener(new TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int co,int af){ if(!internal) before=s.toString(); } public void onTextChanged(CharSequence s,int st,int be,int co){} public void afterTextChanged(Editable e){ if(!internal&&!before.equals(e.toString())){ if(undo.size()>50) undo.removeLast(); undo.push(before);} refresh(); }});
        LinearLayout actions=row(); actions.setPadding(0,dp(12),0,0); Button u=button("↶ "+t("undo"),0xFFF5F3FF,0xFF1B1F3B,12); u.setOnClickListener(v->undo()); actions.addView(u,new LinearLayout.LayoutParams(0,dp(46),1));
        Button em=button("😊 "+t("emoji"),0xFFEFF6FF,0xFF1B1F3B,12); em.setOnClickListener(v->emojis()); LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(0,dp(46),1); ep.setMargins(dp(8),0,0,0); actions.addView(em,ep);
        Button cl=button("⌫ "+t("clear"),0xFFFFF1F2,0xFF1B1F3B,12); cl.setOnClickListener(v->{ if(editor.length()>0){undo.push(editor.getText().toString()); setText("");}}); LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,dp(46),1); cp.setMargins(dp(8),0,0,0); actions.addView(cl,cp); c.addView(actions); return c;
    }
    private View stylesCard(){
        LinearLayout c=card(); c.addView(title(t("styles"))); gap(c,8);
        c.addView(styleRow(new Item[]{
            new Item("Abc","normal",TextStyle.NORMAL),new Item("𝗔𝗯𝗰","bold",TextStyle.BOLD),new Item("𝘈𝘣𝘤","italic",TextStyle.ITALIC),new Item("𝘼𝙗𝙘","bolditalic",TextStyle.BOLDITALIC),new Item("𝐀𝐛𝐜","serif",TextStyle.SERIF),new Item("𝙰𝚋𝚌","mono",TextStyle.MONO),new Item("𝔸𝕓𝕔","double",TextStyle.DOUBLE),new Item("𝒜𝒷𝒸","script",TextStyle.SCRIPT),new Item("ᴀʙᴄ","small",TextStyle.SMALL),new Item("Ａｂｃ","full",TextStyle.FULL),new Item("Ⓐⓑⓒ","circle",TextStyle.CIRCLE)
        }));
        TextView d=title(t("decor")); d.setPadding(0,dp(18),0,dp(8)); c.addView(d);
        c.addView(styleRow(new Item[]{new Item("A̲b̲c̲","underline",TextStyle.UNDER),new Item("A̶b̶c̶","strike",TextStyle.STRIKE),new Item("A̅b̅c̅","over",TextStyle.OVER),new Item("🟨 Abc","highlight",TextStyle.HIGHLIGHT),new Item("✨ Abc","spark",TextStyle.SPARK),new Item("💖 Abc","heart",TextStyle.HEART),new Item("【Abc】","frame",TextStyle.FRAME)})); return c;
    }
    private View styleRow(Item[] items){
        HorizontalScrollView hs=new HorizontalScrollView(this); hs.setHorizontalScrollBarEnabled(false); LinearLayout r=row(); hs.addView(r);
        for(int i=0;i<items.length;i++){ Item it=items[i]; Button b=button(it.sample+"\n"+t(it.key),PALETTE[i%PALETTE.length],0xFF1B1F3B,13); b.setMinWidth(dp(112)); b.setOnClickListener(v->apply(it.style)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,dp(70)); if(i>0)p.setMargins(dp(8),0,0,0); r.addView(b,p);} return hs;
    }
    private View previewCard(){
        LinearLayout c=card(); c.setBackground(round(0xFFFFFDF7,22,0xFFF1D98C,1)); c.addView(title(t("preview"))); gap(c,8); preview=text(t("preview_empty"),20,0xFF667085); preview.setTextIsSelectable(true); preview.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG); preview.setGravity(Gravity.START); preview.setMinHeight(dp(100)); preview.setPadding(dp(14),dp(14),dp(14),dp(14)); preview.setBackground(round(Color.WHITE,16,0xFFEEE4C6,1)); c.addView(preview,new LinearLayout.LayoutParams(-1,-2)); return c;
    }
    private View shareCard(){
        LinearLayout c=card(); HorizontalScrollView hs=new HorizontalScrollView(this); hs.setHorizontalScrollBarEnabled(false); LinearLayout sr=row(); hs.addView(sr);
        social(sr,"WhatsApp","com.whatsapp",0xFFE8FFF4); social(sr,"LinkedIn","com.linkedin.android",0xFFEAF4FF); social(sr,"Facebook","com.facebook.katana",0xFFEDF2FF); social(sr,"X","com.twitter.android",0xFFF1F3F5); social(sr,"Instagram","com.instagram.android",0xFFFFEFF7); social(sr,"Threads","com.instagram.barcelona",0xFFF5F5F5); c.addView(hs);
        LinearLayout a=row(); a.setPadding(0,dp(14),0,0); Button cp=button("⧉  "+t("copy"),0xFF14B8A6,Color.WHITE,15); cp.setTypeface(Typeface.DEFAULT_BOLD); cp.setOnClickListener(v->copy()); a.addView(cp,new LinearLayout.LayoutParams(0,dp(56),1)); Button sh=button("↗  "+t("share"),0xFF7C3AED,Color.WHITE,15); sh.setTypeface(Typeface.DEFAULT_BOLD); sh.setOnClickListener(v->share(null)); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(0,dp(56),1.35f); sp.setMargins(dp(10),0,0,0); a.addView(sh,sp); c.addView(a); return c;
    }
    private void social(LinearLayout r,String label,String pkg,int bg){ Button b=button(label,bg,0xFF1B1F3B,13); b.setOnClickListener(v->share(pkg)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-2,dp(46)); if(r.getChildCount()>0)p.setMargins(dp(8),0,0,0); r.addView(b,p); }

    private void apply(TextStyle s){ String all=editor.getText().toString(); if(all.isEmpty()){toast(t("empty"));return;} int a=Math.max(0,editor.getSelectionStart()),b=Math.max(0,editor.getSelectionEnd()); if(a>b){int x=a;a=b;b=x;} if(a==b){a=0;b=all.length();} String selected=all.substring(a,b),styled=UnicodeStyler.apply(selected,s),out=all.substring(0,a)+styled+all.substring(b); undo.push(all); setText(out); editor.setSelection(Math.min(out.length(),a+styled.length())); }
    private void undo(){ if(!undo.isEmpty()){ String x=undo.pop(); setText(x); editor.setSelection(editor.length()); } }
    private void setText(String s){ internal=true; editor.setText(s); internal=false; refresh(); }
    private void refresh(){ if(editor==null||preview==null)return; String s=editor.getText().toString(); count.setText(s.codePointCount(0,s.length())+" "+t("chars")); preview.setText(s.isEmpty()?t("preview_empty"):s); preview.setTextColor(s.isEmpty()?0xFF667085:0xFF1B1F3B); }
    private void copy(){ String s=editor.getText().toString(); if(s.trim().isEmpty()){toast(t("empty"));return;} ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("TextAura",s)); toast(t("copied")); }
    private void share(String pkg){ String s=editor.getText().toString(); if(s.trim().isEmpty()){toast(t("empty"));return;} Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(Intent.EXTRA_TEXT,s); if(pkg!=null)i.setPackage(pkg); if(pkg!=null&&i.resolveActivity(getPackageManager())==null){toast(t("app_missing")); share(null);return;} startActivity(pkg==null?Intent.createChooser(i,t("share_with")):i); }
    private void restore(Intent i){ String s=null; if(i!=null&&Intent.ACTION_SEND.equals(i.getAction())){CharSequence q=i.getCharSequenceExtra(Intent.EXTRA_TEXT);if(q!=null)s=q.toString();} if(s==null)s=getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_DRAFT,""); if(!s.isEmpty()){setText(s);editor.setSelection(editor.length());} }
    private void emojis(){
        String[] es={"😀","😃","😄","😁","😂","🥰","😍","🤩","😊","😉","😎","🤗","🤔","😅","🥳","😢","😭","😡","🤯","🙌","👏","👍","🙏","💪","🤝","👋","✍️","💡","🚀","🔥","✨","⭐","🌟","💫","❤️","🧡","💛","💚","💙","💜","💖","💕","💯","✅","❌","⚠️","📌","📣","🎯","🏆","🎉","🎁","📚","💼","📈","🌍","🌞","🌈","☕","🍀","🌺","🦋","🕊️","🚗","✈️","📱","💻"};
        GridLayout g=new GridLayout(this);g.setColumnCount(6);g.setPadding(dp(8),dp(8),dp(8),dp(8)); ScrollView sc=new ScrollView(this);sc.addView(g); AlertDialog d=new AlertDialog.Builder(this).setTitle(t("emoji_title")).setView(sc).setNegativeButton(t("close"),null).create();
        for(String e:es){TextView v=text(e,27,Color.BLACK);v.setGravity(Gravity.CENTER);v.setBackground(round(0xFFF8F8FD,12,0xFFE6E6EF,1));GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=dp(50);p.height=dp(50);p.setMargins(dp(4),dp(4),dp(4),dp(4));g.addView(v,p);v.setOnClickListener(x->{insert(((TextView)x).getText().toString());d.dismiss();});}d.show();
    }
    private void insert(String e){int a=Math.max(0,editor.getSelectionStart()),b=Math.max(0,editor.getSelectionEnd());if(a>b){int x=a;a=b;b=x;}String old=editor.getText().toString(),out=old.substring(0,a)+e+old.substring(b);undo.push(old);setText(out);editor.setSelection(a+e.length());}
    private void languages(){String[] names={"Français","English","Español","Deutsch","العربية"},codes={"fr","en","es","de","ar"};new AlertDialog.Builder(this).setTitle(t("language")).setSingleChoiceItems(names,langIndex,(d,w)->{getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString(KEY_LANG,codes[w]).apply();d.dismiss();recreate();}).show();}

    private String currentCode(){return getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_LANG,Locale.getDefault().getLanguage());}
    private int indexOf(String c){if("en".equals(c))return 1;if("es".equals(c))return 2;if("de".equals(c))return 3;if("ar".equals(c))return 4;return 0;}
    private String t(String k){return Texts.get(k,langIndex);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private LinearLayout col(){LinearLayout v=new LinearLayout(this);v.setOrientation(LinearLayout.VERTICAL);return v;}
    private LinearLayout row(){LinearLayout v=new LinearLayout(this);v.setOrientation(LinearLayout.HORIZONTAL);return v;}
    private LinearLayout card(){LinearLayout c=col();c.setPadding(dp(16),dp(16),dp(16),dp(16));c.setBackground(round(Color.WHITE,22,0xFFE9E8F2,1));c.setElevation(dp(3));c.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));return c;}
    private TextView title(String s){TextView v=text(s,18,0xFF1B1F3B);v.setTypeface(Typeface.DEFAULT_BOLD);return v;}
    private TextView text(String s,float z,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(color);return v;}
    private Button button(String s,int bg,int fg,float z){Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(z);b.setTextColor(fg);b.setPadding(dp(12),0,dp(12),0);b.setMinWidth(0);b.setMinHeight(0);b.setBackground(round(bg,15,0xFFE2E2EC,1));return b;}
    private void gap(LinearLayout v,int n){View x=new View(this);v.addView(x,new LinearLayout.LayoutParams(1,dp(n)));}
    private GradientDrawable round(int color,int rad,int stroke,int sw){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(rad));if(sw>0)d.setStroke(dp(sw),stroke);return d;}
    private GradientDrawable gradient(int a,int b,int rad){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{a,b});d.setCornerRadius(dp(rad));return d;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}

    private static final class Item{final String sample,key;final TextStyle style;Item(String s,String k,TextStyle st){sample=s;key=k;style=st;}}
}
