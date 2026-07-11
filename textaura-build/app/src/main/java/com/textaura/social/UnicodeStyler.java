package com.textaura.social;

import java.util.HashMap;
import java.util.Map;

final class UnicodeStyler {
    private static final String U="ABCDEFGHIJKLMNOPQRSTUVWXYZ", L="abcdefghijklmnopqrstuvwxyz", D="0123456789";
    private static final Map<TextStyle,Map<Integer,String>> TABLES=new HashMap<>();
    private static final Map<Integer,String> REVERSE=new HashMap<>();
    static {
        register(TextStyle.BOLD,"𝗔𝗕𝗖𝗗𝗘𝗙𝗚𝗛𝗜𝗝𝗞𝗟𝗠𝗡𝗢𝗣𝗤𝗥𝗦𝗧𝗨𝗩𝗪𝗫𝗬𝗭","𝗮𝗯𝗰𝗱𝗲𝗳𝗴𝗵𝗶𝗷𝗸𝗹𝗺𝗻𝗼𝗽𝗾𝗿𝘀𝘁𝘂𝘃𝘄𝘅𝘆𝘇","𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵");
        register(TextStyle.ITALIC,"𝘈𝘉𝘊𝘋𝘌𝘍𝘎𝘏𝘐𝘑𝘒𝘓𝘔𝘕𝘖𝘗𝘘𝘙𝘚𝘛𝘜𝘝𝘞𝘟𝘠𝘡","𝘢𝘣𝘤𝘥𝘦𝘧𝘨𝘩𝘪𝘫𝘬𝘭𝘮𝘯𝘰𝘱𝘲𝘳𝘴𝘵𝘶𝘷𝘸𝘹𝘺𝘻",D);
        register(TextStyle.BOLDITALIC,"𝘼𝘽𝘾𝘿𝙀𝙁𝙂𝙃𝙄𝙅𝙆𝙇𝙈𝙉𝙊𝙋𝙌𝙍𝙎𝙏𝙐𝙑𝙒𝙓𝙔𝙕","𝙖𝙗𝙘𝙙𝙚𝙛𝙜𝙝𝙞𝙟𝙠𝙡𝙢𝙣𝙤𝙥𝙦𝙧𝙨𝙩𝙪𝙫𝙬𝙭𝙮𝙯","𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵");
        register(TextStyle.SERIF,"𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙","𝐚𝐛𝐜𝐝𝐞𝐟𝐠𝐡𝐢𝐣𝐤𝐥𝐦𝐧𝐨𝐩𝐪𝐫𝐬𝐭𝐮𝐯𝐰𝐱𝐲𝐳","𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗");
        register(TextStyle.MONO,"𝙰𝙱𝙲𝙳𝙴𝙵𝙶𝙷𝙸𝙹𝙺𝙻𝙼𝙽𝙾𝙿𝚀𝚁𝚂𝚃𝚄𝚅𝚆𝚇𝚈𝚉","𝚊𝚋𝚌𝚍𝚎𝚏𝚐𝚑𝚒𝚓𝚔𝚕𝚖𝚗𝚘𝚙𝚚𝚛𝚜𝚝𝚞𝚟𝚠𝚡𝚢𝚣","𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿");
        register(TextStyle.DOUBLE,"𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ","𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫","𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡");
        register(TextStyle.SCRIPT,"𝒜ℬ𝒞𝒟ℰℱ𝒢ℋℐ𝒥𝒦ℒℳ𝒩𝒪𝒫𝒬ℛ𝒮𝒯𝒰𝒱𝒲𝒳𝒴𝒵","𝒶𝒷𝒸𝒹ℯ𝒻ℊ𝒽𝒾𝒿𝓀𝓁𝓂𝓃ℴ𝓅𝓆𝓇𝓈𝓉𝓊𝓋𝓌𝓍𝓎𝓏",D);
        register(TextStyle.SMALL,U,"ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ",D);
        register(TextStyle.CIRCLE,"ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ","ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ","⓪①②③④⑤⑥⑦⑧⑨");
    }
    private UnicodeStyler(){}
    static String apply(String input,TextStyle style){
        if(style==TextStyle.NORMAL)return normal(input);
        if(style==TextStyle.FULL)return full(normal(input));
        if(style==TextStyle.UNDER)return mark(input,0x332);
        if(style==TextStyle.STRIKE)return mark(input,0x336);
        if(style==TextStyle.OVER)return mark(input,0x305);
        if(style==TextStyle.HIGHLIGHT)return "🟨 "+input+" 🟨";
        if(style==TextStyle.SPARK)return "✨ "+input+" ✨";
        if(style==TextStyle.HEART)return "💖 "+input+" 💖";
        if(style==TextStyle.FRAME)return "【"+input+"】";
        Map<Integer,String> map=TABLES.get(style); StringBuilder out=new StringBuilder();
        for(int cp:normal(input).codePoints().toArray()){String mapped=map==null?null:map.get(cp);if(mapped==null)out.appendCodePoint(cp);else out.append(mapped);}
        return out.toString();
    }
    static String normal(String input){
        String x=unwrap(input,"🟨 "," 🟨");x=unwrap(x,"✨ "," ✨");x=unwrap(x,"💖 "," 💖");x=unwrap(x,"【","】");
        StringBuilder out=new StringBuilder();
        for(int cp:x.codePoints().toArray()){if(cp==0x332||cp==0x336||cp==0x305)continue;String mapped=REVERSE.get(cp);if(mapped!=null)out.append(mapped);else if(cp==0x3000)out.append(' ');else if(cp>=0xFF01&&cp<=0xFF5E)out.appendCodePoint(cp-0xFEE0);else out.appendCodePoint(cp);}
        return out.toString();
    }
    private static String unwrap(String x,String prefix,String suffix){return x.startsWith(prefix)&&x.endsWith(suffix)&&x.length()>=prefix.length()+suffix.length()?x.substring(prefix.length(),x.length()-suffix.length()):x;}
    private static String mark(String x,int mark){StringBuilder out=new StringBuilder();for(int cp:x.codePoints().toArray()){out.appendCodePoint(cp);if(Character.isLetterOrDigit(cp))out.appendCodePoint(mark);}return out.toString();}
    private static String full(String x){StringBuilder out=new StringBuilder();for(int cp:x.codePoints().toArray()){if(cp==32)out.appendCodePoint(0x3000);else if(cp>=33&&cp<=126)out.appendCodePoint(cp+0xFEE0);else out.appendCodePoint(cp);}return out.toString();}
    private static void register(TextStyle style,String upper,String lower,String digits){Map<Integer,String> map=new HashMap<>();add(map,U,upper);add(map,L,lower);add(map,D,digits);TABLES.put(style,map);reverse(U,upper);reverse(L,lower);reverse(D,digits);}
    private static void add(Map<Integer,String> map,String source,String target){int[] a=source.codePoints().toArray(),b=target.codePoints().toArray();for(int i=0;i<Math.min(a.length,b.length);i++)map.put(a[i],new String(Character.toChars(b[i])));}
    private static void reverse(String source,String target){int[] a=source.codePoints().toArray(),b=target.codePoints().toArray();for(int i=0;i<Math.min(a.length,b.length);i++)REVERSE.put(b[i],new String(Character.toChars(a[i])));}
}
