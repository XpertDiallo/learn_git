package com.textaura.social;

import java.util.HashMap;
import java.util.Map;

final class Texts {
    private static final Map<String,String[]> DATA = new HashMap<>();
    static {
        put("tag","Donnez du style à vos publications ✨","Give your posts some style ✨","Da estilo a tus publicaciones ✨","Verleihe deinen Beiträgen Stil ✨","أضف لمسة جميلة إلى منشوراتك ✨");
        put("your_text","Votre texte","Your text","Tu texto","Dein Text","نصك");
        put("hint","Écrivez ou collez votre publication ici…","Write or paste your post here…","Escribe o pega tu publicación aquí…","Schreibe oder füge deinen Beitrag hier ein…","اكتب أو الصق منشورك هنا…");
        put("tip","Sélectionnez un passage, puis touchez un style. Sans sélection, le style s’applique à tout le texte.","Select a passage, then tap a style. With no selection, it applies to all text.","Selecciona un fragmento y toca un estilo. Sin selección, se aplica a todo el texto.","Markiere einen Abschnitt und tippe auf einen Stil. Ohne Auswahl gilt er für den ganzen Text.","حدّد مقطعًا ثم اختر نمطًا. من دون تحديد، يُطبّق على النص كله.");
        put("styles","Styles rapides","Quick styles","Estilos rápidos","Schnelle Stile","أنماط سريعة");
        put("decor","Décorations","Decorations","Decoraciones","Dekorationen","زخارف");
        put("preview","Aperçu","Preview","Vista previa","Vorschau","معاينة");
        put("preview_empty","Votre texte stylisé apparaîtra ici.","Your styled text will appear here.","Tu texto con estilo aparecerá aquí.","Dein formatierter Text erscheint hier.","سيظهر النص المنسّق هنا.");
        put("copy","Copier","Copy","Copiar","Kopieren","نسخ");
        put("share","Publier / partager","Post / share","Publicar / compartir","Posten / teilen","نشر / مشاركة");
        put("emoji","Emojis","Emojis","Emojis","Emojis","رموز تعبيرية");
        put("clear","Effacer","Clear","Borrar","Löschen","مسح");
        put("undo","Annuler","Undo","Deshacer","Rückgängig","تراجع");
        put("chars","caractères","characters","caracteres","Zeichen","حرفًا");
        put("empty","Écrivez d’abord un texte.","Write some text first.","Primero escribe un texto.","Schreibe zuerst einen Text.","اكتب نصًا أولًا.");
        put("copied","Texte copié","Text copied","Texto copiado","Text kopiert","تم نسخ النص");
        put("share_with","Partager avec…","Share with…","Compartir con…","Teilen mit…","مشاركة عبر…");
        put("privacy","🔒 Hors ligne, sans compte. Vos textes restent sur votre téléphone.","🔒 Offline, no account. Your texts stay on your phone.","🔒 Sin conexión ni cuenta. Tus textos permanecen en tu teléfono.","🔒 Offline, ohne Konto. Deine Texte bleiben auf deinem Telefon.","🔒 يعمل دون إنترنت أو حساب. تبقى نصوصك على هاتفك.");
        put("app_missing","Application non installée : ouverture du menu de partage.","App not installed: opening the share menu.","Aplicación no instalada: se abre el menú de compartir.","App nicht installiert: Das Teilen-Menü wird geöffnet.","التطبيق غير مثبت: سيتم فتح قائمة المشاركة.");
        put("normal","Normal","Normal","Normal","Normal","عادي");
        put("bold","Gras","Bold","Negrita","Fett","عريض");
        put("italic","Italique","Italic","Cursiva","Kursiv","مائل");
        put("bolditalic","Gras italique","Bold italic","Negrita cursiva","Fett kursiv","عريض مائل");
        put("serif","Gras serif","Serif bold","Negrita serif","Serifenfett","عريض مزخرف");
        put("mono","Monospace","Monospace","Monoespaciado","Monospace","أحادي المسافة");
        put("double","Double trait","Double-struck","Doble trazo","Doppelstrich","خط مزدوج");
        put("script","Cursif","Script","Caligrafía","Schreibschrift","خط يدوي");
        put("small","Petites capitales","Small caps","Versalitas","Kapitälchen","حروف صغيرة كبيرة");
        put("full","Pleine largeur","Full width","Ancho completo","Vollbreite","عرض كامل");
        put("circle","Cerclé","Circled","Círculos","Eingekreist","داخل دوائر");
        put("underline","Souligné","Underline","Subrayado","Unterstrichen","تحته خط");
        put("strike","Barré","Strikethrough","Tachado","Durchgestrichen","يتوسطه خط");
        put("over","Surligne haute","Overline","Línea superior","Überstrichen","فوقه خط");
        put("highlight","Surligné jaune","Yellow highlight","Resaltado amarillo","Gelb markiert","تمييز أصفر");
        put("spark","Étincelles","Sparkles","Destellos","Funkeln","بريق");
        put("heart","Cœurs","Hearts","Corazones","Herzen","قلوب");
        put("frame","Encadré","Framed","Enmarcado","Eingerahmt","داخل إطار");
        put("language","Langue","Language","Idioma","Sprache","اللغة");
        put("emoji_title","Choisir un emoji","Choose an emoji","Elegir un emoji","Emoji wählen","اختر رمزًا تعبيريًا");
        put("close","Fermer","Close","Cerrar","Schließen","إغلاق");
    }
    private Texts() {}
    private static void put(String key,String fr,String en,String es,String de,String ar){DATA.put(key,new String[]{fr,en,es,de,ar});}
    static String get(String key,int languageIndex){String[] values=DATA.get(key);return values==null?key:values[Math.max(0,Math.min(languageIndex,values.length-1))];}
}
