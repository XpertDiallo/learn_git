package com.textaura.social;

import java.util.HashMap;
import java.util.Map;

final class Texts {
    private static final Map<String,String[]> DATA = new HashMap<>();

    static {
        put("tag","Donnez du style à vos publications ✨","Give your posts some style ✨","Da estilo a tus publicaciones ✨","Verleihe deinen Beiträgen Stil ✨","أضف لمسة جميلة إلى منشوراتك ✨");
        put("your_text","Votre texte","Your text","Tu texto","Dein Text","نصك");
        put("hint","Écrivez ou collez votre publication ici…","Write or paste your post here…","Escribe o pega tu publicación aquí…","Schreibe oder füge deinen Beitrag hier ein…","اكتب أو الصق منشورك هنا…");
        put("tip","Sélectionnez un passage : il reste en surbrillance pendant le choix d’un outil. Sans sélection, TextAura vous demande la cible.","Select a passage: it stays highlighted while you choose a tool. Without a selection, TextAura asks for the target.","Selecciona un fragmento: queda resaltado mientras eliges una herramienta. Sin selección, TextAura pregunta el destino.","Markiere einen Abschnitt: Er bleibt hervorgehoben, während du ein Werkzeug wählst. Ohne Auswahl fragt TextAura nach dem Ziel.","حدّد مقطعًا: يبقى مظللًا أثناء اختيار الأداة. من دون تحديد يسألك TextAura عن النطاق.");
        put("chars","caractères","characters","caracteres","Zeichen","حرفًا");
        put("words","mots","words","palabras","Wörter","كلمات");
        put("lines","lignes","lines","líneas","Zeilen","أسطر");
        put("selected_chars","caractères sélectionnés","selected characters","caracteres seleccionados","ausgewählte Zeichen","أحرف محددة");
        put("hide_keyboard","Masquer le clavier","Hide keyboard","Ocultar teclado","Tastatur ausblenden","إخفاء لوحة المفاتيح");
        put("selection_saved","Sélection conservée","Selection preserved","Selección conservada","Auswahl gespeichert","تم حفظ التحديد");

        put("styles","Styles","Styles","Estilos","Stile","الأنماط");
        put("decor","Décorations","Decorations","Decoraciones","Dekorationen","الزخارف");
        put("fonts","Police","Fonts","Fuentes","Schriften","الخطوط");
        put("formats","Casse et formats","Case and formats","Mayúsculas y formatos","Großschreibung und Formate","حالة الأحرف والتنسيقات");
        put("colors","Couleurs","Colors","Colores","Farben","الألوان");
        put("paragraph_tools","Alignement et listes","Alignment and lists","Alineación y listas","Ausrichtung und Listen","المحاذاة والقوائم");
        put("preview","Aperçu","Preview","Vista previa","Vorschau","معاينة");
        put("preview_empty","Votre texte stylisé apparaîtra ici.","Your styled text will appear here.","Tu texto con estilo aparecerá aquí.","Dein formatierter Text erscheint hier.","سيظهر النص المنسق هنا.");
        put("share","Partager","Share","Compartir","Teilen","مشاركة");
        put("copy","Copier le texte","Copy text","Copiar texto","Text kopieren","نسخ النص");
        put("share_image","Partager ou exporter en image","Share or export as image","Compartir o exportar como imagen","Als Bild teilen oder exportieren","مشاركة أو تصدير كصورة");
        put("privacy","🔒 100 % hors ligne, sans compte, sans publicité et sans traçage.","🔒 100% offline, account-free, ad-free and tracking-free.","🔒 100 % sin conexión, sin cuenta, publicidad ni rastreo.","🔒 100 % offline, ohne Konto, Werbung oder Tracking.","🔒 يعمل دون اتصال أو حساب أو إعلانات أو تتبع.");
        put("rich_note","Les couleurs, polices, alignements, listes et traits continus sont conservés dans l’image. Le texte simple conserve les styles Unicode et les puces.","Colors, fonts, alignment, lists and continuous lines are preserved in the image. Plain text preserves Unicode styles and bullets.","Los colores, las fuentes, la alineación, las listas y las líneas continuas se conservan en la imagen. El texto simple conserva los estilos Unicode y las viñetas.","Farben, Schriften, Ausrichtung, Listen und durchgehende Linien bleiben im Bild erhalten. Einfacher Text behält Unicode-Stile und Aufzählungen.","تُحفظ الألوان والخطوط والمحاذاة والقوائم والخطوط المتصلة في الصورة، بينما يحافظ النص العادي على أنماط يونيكود والتعداد.");

        put("undo","Annuler","Undo","Deshacer","Rückgängig","تراجع");
        put("redo","Rétablir","Redo","Rehacer","Wiederholen","إعادة");
        put("emoji","Emojis","Emojis","Emojis","Emojis","رموز تعبيرية");
        put("clear","Effacer","Clear","Borrar","Löschen","مسح");
        put("clear_confirm","Effacer tout le texte et sa mise en forme ?","Clear all text and formatting?","¿Borrar todo el texto y su formato?","Gesamten Text und Formatierung löschen?","مسح النص وكل تنسيقاته؟");
        put("copy_selection","Copier","Copy","Copiar","Kopieren","نسخ");
        put("clear_format","Nettoyer","Clear format","Limpiar formato","Format löschen","مسح التنسيق");
        put("empty","Écrivez d’abord un texte.","Write some text first.","Primero escribe un texto.","Schreibe zuerst einen Text.","اكتب نصًا أولًا.");
        put("copied","Texte copié","Text copied","Texto copiado","Text kopiert","تم نسخ النص");
        put("select_text_first","Sélectionnez d’abord un passage.","Select a passage first.","Selecciona primero un fragmento.","Wähle zuerst einen Abschnitt aus.","حدّد مقطعًا أولًا.");

        put("normal","Normal","Normal","Normal","Normal","عادي");
        put("bold","Gras","Bold","Negrita","Fett","عريض");
        put("italic","Italique","Italic","Cursiva","Kursiv","مائل");
        put("bolditalic","Gras italique","Bold italic","Negrita cursiva","Fett kursiv","عريض مائل");
        put("serif","Gras serif","Serif bold","Negrita serif","Serifenfett","سيريف عريض");
        put("mono","Monospace","Monospace","Monoespaciado","Monospace","أحادي المسافة");
        put("double","Double trait","Double-struck","Doble trazo","Doppelstrich","خط مزدوج");
        put("script","Cursif","Script","Caligrafía","Schreibschrift","خط يدوي");
        put("small","Petites capitales","Small caps","Versalitas","Kapitälchen","حروف صغيرة كبيرة");
        put("full","Pleine largeur","Full width","Ancho completo","Vollbreite","عرض كامل");
        put("circle","Cerclé","Circled","Círculos","Eingekreist","داخل دوائر");
        put("underline","Souligné continu","Continuous underline","Subrayado continuo","Durchgehend unterstrichen","تسطير متصل");
        put("strike","Barré continu","Continuous strikethrough","Tachado continuo","Durchgehend durchgestrichen","شطب متصل");
        put("over","Ligne supérieure","Overline","Línea superior","Überstrichen","خط علوي");
        put("highlight","Surligneur continu","Continuous highlight","Resaltado continuo","Durchgehende Markierung","تظليل متصل");
        put("spark","Étincelles","Sparkles","Destellos","Funkeln","بريق");
        put("heart","Cœurs","Hearts","Corazones","Herzen","قلوب");
        put("frame","Encadré","Framed","Enmarcado","Eingerahmt","إطار");

        put("font_note","Touchez une police rapide ou ouvrez le catalogue. La police s’applique uniquement à la sélection et reste fidèle dans l’aperçu et l’image.","Tap a quick font or open the catalog. The font applies only to the selection and remains faithful in preview and image.","Toca una fuente rápida o abre el catálogo. La fuente se aplica solo a la selección y se conserva en la vista previa y la imagen.","Tippe auf eine Schnellschrift oder öffne den Katalog. Die Schrift gilt nur für die Auswahl und bleibt in Vorschau und Bild erhalten.","اختر خطًا سريعًا أو افتح الدليل. يُطبّق الخط على التحديد فقط ويظهر في المعاينة والصورة.");
        put("browse_fonts","Parcourir toutes les polices","Browse all fonts","Explorar todas las fuentes","Alle Schriften durchsuchen","استعراض كل الخطوط");
        put("font_library","Bibliothèque de polices","Font library","Biblioteca de fuentes","Schriftbibliothek","مكتبة الخطوط");
        put("font_legal_note","TextAura n’intègre aucun fichier de police propriétaire. Les noms demandés utilisent des alternatives système sûres et des fallbacks compatibles.","TextAura embeds no proprietary font files. Requested names use safe system alternatives and compatible fallbacks.","TextAura no integra archivos de fuentes propietarias. Los nombres solicitados usan alternativas seguras del sistema y fuentes de respaldo compatibles.","TextAura bindet keine proprietären Schriftdateien ein. Die gewünschten Namen verwenden sichere Systemalternativen und kompatible Fallbacks.","لا يدمج TextAura ملفات خطوط مملوكة. تستخدم الأسماء المطلوبة بدائل نظام آمنة وخطوطًا احتياطية متوافقة.");
        put("search_font","Rechercher une police…","Search fonts…","Buscar una fuente…","Schrift suchen…","ابحث عن خط…");
        put("font_all","Toutes","All","Todas","Alle","الكل");
        put("font_recent","Récentes","Recent","Recientes","Zuletzt","الأخيرة");
        put("font_favorites","Favoris","Favorites","Favoritas","Favoriten","المفضلة");
        put("font_system","Système","System","Sistema","System","النظام");
        put("font_classics","Classiques","Classics","Clásicas","Klassiker","كلاسيكية");
        put("font_humanes","Humanes","Humanist serif","Humanas","Humanistische Antiqua","إنسانية");
        put("font_garaldes","Garaldes","Garaldes","Garaldas","Garalden","غارالد");
        put("font_reales","Réales","Transitional","Reales","Übergangsantiqua","انتقالية");
        put("font_didones","Didones","Didones","Didonas","Didonen","ديدون");
        put("font_mecanes","Mécanes","Slab serif","Mecanas","Egyptienne","ميكانيكية");
        put("font_lineales","Linéales","Sans serif","Lineales","Serifenlos","خطوط بلا زوائد");
        put("font_incises","Incises","Glyphic","Incisas","Glyphisch","منقوشة");
        put("font_scriptes","Scriptes","Scripts","Script","Schreibschriften","كتابية");
        put("font_manuaires","Manuaires","Handwritten","Manuales","Handschriften","يدوية");
        put("font_fractures","Fractures","Blackletter","Fracturas","Gebrochene Schriften","قوطية");
        put("font_non_latin","Non latines","Non-Latin","No latinas","Nicht-lateinisch","غير لاتينية");
        put("no_font","Aucune police ne correspond.","No font matches.","Ninguna fuente coincide.","Keine passende Schrift.","لا يوجد خط مطابق.");
        put("font_fallback_warning","Cette police utilise un fallback compatible pour l’arabe.","This font uses a compatible fallback for Arabic.","Esta fuente usa una alternativa compatible para árabe.","Diese Schrift nutzt für Arabisch einen kompatiblen Fallback.","يستخدم هذا الخط خطًا احتياطيًا متوافقًا مع العربية.");
        put("added_favorite","Ajouté aux favoris","Added to favorites","Añadido a favoritos","Zu Favoriten hinzugefügt","تمت الإضافة إلى المفضلة");
        put("removed_favorite","Retiré des favoris","Removed from favorites","Eliminado de favoritos","Aus Favoriten entfernt","تمت الإزالة من المفضلة");

        put("casing","Casse et majuscules","Casing","Mayúsculas y minúsculas","Groß- und Kleinschreibung","حالة الأحرف");
        put("naming","Conventions informatiques","Naming conventions","Convenciones de nombres","Benennungskonventionen","تسمية برمجية");
        put("cleanup","Nettoyage et publication","Cleanup and publishing","Limpieza y publicación","Bereinigung und Veröffentlichung","تنظيف ونشر");
        put("sentence","Phrase","Sentence case","Frase","Satzschreibung","حرف أول كبير");
        put("lower","minuscules","lowercase","minúsculas","kleinbuchstaben","أحرف صغيرة");
        put("upper","MAJUSCULES","UPPERCASE","MAYÚSCULAS","GROSSBUCHSTABEN","أحرف كبيرة");
        put("title","Nom Propre","Title Case","Tipo Título","Jedes Wort groß","أول كل كلمة كبير");
        put("toggle","Inverser la casse","Toggle case","Invertir mayúsculas","Groß/Klein umkehren","عكس الحالة");
        put("sponge","Aléatoire","SpongeBob case","Alternado","Abwechselnd","متناوب");
        put("snake","snake_case","snake_case","snake_case","snake_case","snake_case");
        put("kebab","kebab-case","kebab-case","kebab-case","kebab-case","kebab-case");
        put("camel","camelCase","camelCase","camelCase","camelCase","camelCase");
        put("pascal","PascalCase","PascalCase","PascalCase","PascalCase","PascalCase");
        put("no_accents","Sans accents","Remove accents","Sin acentos","Ohne Akzente","دون حركات");
        put("no_spaces","Sans espaces","Remove spaces","Sin espacios","Ohne Leerzeichen","دون مسافات");
        put("trim","Rogner les espaces","Trim spaces","Recortar espacios","Leerzeichen entfernen","قص المسافات");
        put("collapse_spaces","Espaces doubles","Duplicate spaces","Espacios dobles","Doppelte Leerzeichen","المسافات المكررة");
        put("punctuation","Ponctuation propre","Clean punctuation","Puntuación limpia","Saubere Zeichensetzung","تنسيق علامات الترقيم");
        put("clean_lines","Lignes propres","Clean lines","Líneas limpias","Saubere Zeilen","تنظيف الأسطر");
        put("hashtags","Créer des hashtags","Create hashtags","Crear hashtags","Hashtags erstellen","إنشاء وسوم");

        put("text_color","Couleur du texte","Text color","Color del texto","Textfarbe","لون النص");
        put("highlight_color","Couleur du surligneur","Highlight color","Color de resaltado","Markierungsfarbe","لون التظليل");
        put("underline_color","Couleur du soulignement","Underline color","Color de subrayado","Unterstreichungsfarbe","لون التسطير");
        put("clear_colors","Retirer les couleurs","Remove colors","Quitar colores","Farben entfernen","إزالة الألوان");
        put("color_title","Choisir une couleur","Choose a color","Elegir un color","Farbe wählen","اختر لونًا");
        put("apply","Appliquer","Apply","Aplicar","Anwenden","تطبيق");
        put("invalid_color","Code couleur invalide. Exemple : #7C3AED","Invalid color code. Example: #7C3AED","Código de color inválido. Ejemplo: #7C3AED","Ungültiger Farbcode. Beispiel: #7C3AED","رمز اللون غير صالح. مثال: #7C3AED");

        put("alignment","Alignement du texte","Text alignment","Alineación del texto","Textausrichtung","محاذاة النص");
        put("align_left","Aligner à gauche","Align left","Alinear a la izquierda","Linksbündig","محاذاة لليسار");
        put("align_center","Centré","Centered","Centrado","Zentriert","توسيط");
        put("align_right","Aligner à droite","Align right","Alinear a la derecha","Rechtsbündig","محاذاة لليمين");
        put("justify","Justifier le texte","Justify text","Justificar texto","Blocksatz","ضبط النص");
        put("lists","Puces et listes","Bullets and lists","Viñetas y listas","Aufzählungen und Listen","التعداد والقوائم");
        put("list_number","Numérique","Numbered","Numérica","Nummeriert","رقمية");
        put("list_letter","Lettres majuscules","Uppercase letters","Letras mayúsculas","Großbuchstaben","حروف كبيرة");
        put("list_letter_lower","Lettres minuscules","Lowercase letters","Letras minúsculas","Kleinbuchstaben","حروف صغيرة");
        put("list_roman","Romains majuscules","Upper Roman","Romanos mayúsculos","Römisch groß","رومانية كبيرة");
        put("list_roman_lower","Romains minuscules","Lower Roman","Romanos minúsculos","Römisch klein","رومانية صغيرة");
        put("list_bullet","Puce ronde","Round bullet","Viñeta redonda","Runder Punkt","نقطة");
        put("list_circle","Cercle","Circle","Círculo","Kreis","دائرة");
        put("list_square","Carré","Square","Cuadrado","Quadrat","مربع");
        put("list_triangle","Triangle","Triangle","Triángulo","Dreieck","مثلث");
        put("list_dash","Tiret","Dash","Guion","Strich","شرطة");
        put("list_check","Coche","Check mark","Marca","Häkchen","علامة صح");
        put("list_arrow","Flèche","Arrow","Flecha","Pfeil","سهم");
        put("list_star","Étoile","Star","Estrella","Stern","نجمة");
        put("list_diamond","Losange","Diamond","Rombo","Raute","معين");
        put("list_heart","Cœur","Heart","Corazón","Herz","قلب");
        put("list_lightning","Éclair","Lightning","Rayo","Blitz","برق");
        put("list_pin","Punaise","Pin","Chincheta","Pinnadel","دبوس");
        put("list_indent","Augmenter le niveau","Increase level","Aumentar nivel","Ebene erhöhen","زيادة المستوى");
        put("list_outdent","Diminuer le niveau","Decrease level","Disminuir nivel","Ebene verringern","تقليل المستوى");
        put("list_remove","Retirer les puces","Remove bullets","Quitar viñetas","Aufzählung entfernen","إزالة التعداد");

        put("apply_to","Appliquer à…","Apply to…","Aplicar a…","Anwenden auf…","تطبيق على…");
        put("current_word","Mot courant","Current word","Palabra actual","Aktuelles Wort","الكلمة الحالية");
        put("current_paragraph","Paragraphe courant","Current paragraph","Párrafo actual","Aktueller Absatz","الفقرة الحالية");
        put("all_text","Tout le texte","All text","Todo el texto","Gesamter Text","كل النص");
        put("cancel","Annuler","Cancel","Cancelar","Abbrechen","إلغاء");
        put("close","Fermer","Close","Cerrar","Schließen","إغلاق");

        put("share_with","Partager avec…","Share with…","Compartir con…","Teilen mit…","مشاركة عبر…");
        put("email","E-mail","Email","Correo","E-Mail","بريد إلكتروني");
        put("other_apps","Autres applications","Other apps","Otras aplicaciones","Andere Apps","تطبيقات أخرى");
        put("app_missing","Application non installée : ouverture du partage général.","App not installed: opening the general share menu.","Aplicación no instalada: se abre el menú general.","App nicht installiert: Allgemeines Teilen-Menü wird geöffnet.","التطبيق غير مثبت: سيتم فتح قائمة المشاركة العامة.");
        put("share_error","Le partage n’a pas pu être ouvert.","Sharing could not be opened.","No se pudo abrir el uso compartido.","Teilen konnte nicht geöffnet werden.","تعذر فتح المشاركة.");
        put("image_format","Format de l’image","Image format","Formato de imagen","Bildformat","تنسيق الصورة");
        put("image_auto","Hauteur automatique","Automatic height","Altura automática","Automatische Höhe","ارتفاع تلقائي");
        put("image_error","Impossible de générer l’image.","Unable to generate the image.","No se pudo generar la imagen.","Bild konnte nicht erzeugt werden.","تعذر إنشاء الصورة.");

        put("language","Langue","Language","Idioma","Sprache","اللغة");
        put("emoji_title","Bibliothèque d’emojis","Emoji library","Biblioteca de emojis","Emoji-Bibliothek","مكتبة الرموز التعبيرية");
        put("search_emoji","Rechercher un emoji ou une catégorie…","Search an emoji or category…","Buscar un emoji o categoría…","Emoji oder Kategorie suchen…","ابحث عن رمز أو فئة…");
        put("recent_favorites","Favoris et récents","Favorites and recent","Favoritos y recientes","Favoriten und zuletzt","المفضلة والأخيرة");
        put("no_recent_emoji","Appuyez longuement sur un emoji pour l’ajouter aux favoris.","Long-press an emoji to add it to favorites.","Mantén pulsado un emoji para añadirlo a favoritos.","Emoji lange drücken, um es zu den Favoriten hinzuzufügen.","اضغط مطولًا على رمز لإضافته إلى المفضلة.");
        put("emoji_added","Emoji déjà présent dans le texte","Emoji already present in the text","Emoji ya presente en el texto","Emoji bereits im Text vorhanden","الرمز موجود بالفعل في النص");
        put("favorite","Favori","Favorite","Favorito","Favorit","مفضل");
        put("emoji_smileys","Smileys et émotions","Smileys & Emotion","Caritas y emociones","Smileys & Emotionen","الوجوه والمشاعر");
        put("emoji_people","Personnes et corps","People & Body","Personas y cuerpo","Menschen & Körper","الأشخاص والجسم");
        put("emoji_nature","Animaux et nature","Animals & Nature","Animales y naturaleza","Tiere & Natur","الحيوانات والطبيعة");
        put("emoji_food","Nourriture et boissons","Food & Drink","Comida y bebida","Essen & Trinken","الطعام والشراب");
        put("emoji_activities","Activités","Activities","Actividades","Aktivitäten","الأنشطة");
        put("emoji_travel","Voyages et lieux","Travel & Places","Viajes y lugares","Reisen & Orte","السفر والأماكن");
        put("emoji_objects","Objets","Objects","Objetos","Objekte","الأشياء");
        put("emoji_symbols","Symboles","Symbols","Símbolos","Symbole","الرموز");
        put("emoji_flags","Drapeaux","Flags","Banderas","Flaggen","الأعلام");
    }

    private Texts() {}

    private static void put(String key, String fr, String en, String es, String de, String ar) {
        DATA.put(key, new String[] {fr, en, es, de, ar});
    }

    static String get(String key, int languageIndex) {
        String[] values = DATA.get(key);
        if (values == null) return key;
        return values[Math.max(0, Math.min(languageIndex, values.length - 1))];
    }
}
