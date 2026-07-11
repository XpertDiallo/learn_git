export const EMPTY_DOC = { text: '', marks: [], paragraphs: [] };
const U='ABCDEFGHIJKLMNOPQRSTUVWXYZ', L='abcdefghijklmnopqrstuvwxyz', D='0123456789';
const FONTS={
 bold:['𝗔𝗕𝗖𝗗𝗘𝗙𝗚𝗛𝗜𝗝𝗞𝗟𝗠𝗡𝗢𝗣𝗤𝗥𝗦𝗧𝗨𝗩𝗪𝗫𝗬𝗭','𝗮𝗯𝗰𝗱𝗲𝗳𝗴𝗵𝗶𝗷𝗸𝗹𝗺𝗻𝗼𝗽𝗾𝗿𝘀𝘁𝘂𝘃𝘄𝘅𝘆𝘇','𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵'],
 italic:['𝘈𝘉𝘊𝘋𝘌𝘍𝘎𝘏𝘐𝘑𝘒𝘓𝘔𝘕𝘖𝘗𝘘𝘙𝘚𝘛𝘜𝘝𝘞𝘟𝘠𝘡','𝘢𝘣𝘤𝘥𝘦𝘧𝘨𝘩𝘪𝘫𝘬𝘭𝘮𝘯𝘰𝘱𝘲𝘳𝘴𝘵𝘶𝘷𝘸𝘹𝘺𝘻',D],
 boldItalic:['𝘼𝘽𝘾𝘿𝙀𝙁𝙂𝙃𝙄𝙅𝙆𝙇𝙈𝙉𝙊𝙋𝙌𝙍𝙎𝙏𝙐𝙑𝙒𝙓𝙔𝙕','𝙖𝙗𝙘𝙙𝙚𝙛𝙜𝙝𝙞𝙟𝙠𝙡𝙢𝙣𝙤𝙥𝙦𝙧𝙨𝙩𝙪𝙫𝙬𝙭𝙮𝙯','𝟬𝟭𝟮𝟯𝟰𝟱𝟲𝟳𝟴𝟵'],
 serif:['𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙','𝐚𝐛𝐜𝐝𝐞𝐟𝐠𝐡𝐢𝐣𝐤𝐥𝐦𝐧𝐨𝐩𝐪𝐫𝐬𝐭𝐮𝐯𝐰𝐱𝐲𝐳','𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗'],
 mono:['𝙰𝙱𝙲𝙳𝙴𝙵𝙶𝙷𝙸𝙹𝙺𝙻𝙼𝙽𝙾𝙿𝚀𝚁𝚂𝚃𝚄𝚅𝚆𝚇𝚈𝚉','𝚊𝚋𝚌𝚍𝚎𝚏𝚐𝚑𝚒𝚓𝚔𝚕𝚖𝚗𝚘𝚙𝚚𝚛𝚜𝚝𝚞𝚟𝚠𝚡𝚢𝚣','𝟶𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿'],
 double:['𝔸𝔹ℂ𝔻𝔼𝔽𝔾ℍ𝕀𝕁𝕂𝕃𝕄ℕ𝕆ℙℚℝ𝕊𝕋𝕌𝕍𝕎𝕏𝕐ℤ','𝕒𝕓𝕔𝕕𝕖𝕗𝕘𝕙𝕚𝕛𝕜𝕝𝕞𝕟𝕠𝕡𝕢𝕣𝕤𝕥𝕦𝕧𝕨𝕩𝕪𝕫','𝟘𝟙𝟚𝟛𝟜𝟝𝟞𝟟𝟠𝟡'],
 script:['𝒜ℬ𝒞𝒟ℰℱ𝒢ℋℐ𝒥𝒦ℒℳ𝒩𝒪𝒫𝒬ℛ𝒮𝒯𝒰𝒱𝒲𝒳𝒴𝒵','𝒶𝒷𝒸𝒹ℯ𝒻ℊ𝒽𝒾𝒿𝓀𝓁𝓂𝓃ℴ𝓅𝓆𝓇𝓈𝓉𝓊𝓋𝓌𝓍𝓎𝓏',D],
 small:[U,'ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ',D],
 circle:['ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ','ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ','⓪①②③④⑤⑥⑦⑧⑨']
};
const maps={}, reverse=new Map();
for(const [name,[up,lo,dg]] of Object.entries(FONTS)){
 const m=new Map(); [[U,up],[L,lo],[D,dg]].forEach(([src,dst])=>{const a=[...src],b=[...dst];a.forEach((c,i)=>{m.set(c,b[i]??c);reverse.set(b[i]??c,c)})}); maps[name]=m;
}
export function normalText(v){return [...v.replace(/^🟨\s?|\s?🟨$/gu,'').replace(/^✨\s?|\s?✨$/gu,'').replace(/^💖\s?|\s?💖$/gu,'').replace(/^【|】$/gu,'')].filter(c=>!['\u0332','\u0336','\u0305'].includes(c)).map(c=>{const cp=c.codePointAt(0);if(reverse.has(c))return reverse.get(c);if(cp===0x3000)return' ';if(cp>=0xff01&&cp<=0xff5e)return String.fromCodePoint(cp-0xfee0);return c}).join('')}
export function unicodeStyle(v,s){if(s==='normal')return normalText(v);const n=normalText(v);if(s==='full')return[...n].map(c=>{const p=c.codePointAt(0);return c===' '?String.fromCodePoint(0x3000):(p>=33&&p<=126?String.fromCodePoint(p+0xfee0):c)}).join('');return[...n].map(c=>maps[s]?.get(c)??c).join('')}
export function combine(v,mark){return[...v].map(c=>c==='\n'||c==='\r'||/\p{M}/u.test(c)?c:c+mark).join('')}
export function caseTransform(v,op,locale='fr'){
 if(op==='lower')return v.toLocaleLowerCase(locale);if(op==='upper')return v.toLocaleUpperCase(locale);if(op==='noAccents')return v.normalize('NFD').replace(/\p{M}+/gu,'');if(op==='noSpaces')return v.replace(/\s+/gu,'');
 if(op==='sentence'){let cap=true;return[...v.toLocaleLowerCase(locale)].map(c=>{if(cap&&/\p{L}/u.test(c)){cap=false;return c.toLocaleUpperCase(locale)}if(/[.!?\n]/u.test(c))cap=true;return c}).join('')}
 if(op==='title'){let cap=true;return[...v.toLocaleLowerCase(locale)].map(c=>{if(/[\p{L}\p{N}]/u.test(c)){const r=cap?c.toLocaleUpperCase(locale):c;cap=false;return r}cap=true;return c}).join('')}
 if(op==='toggle')return[...v].map(c=>c===c.toLocaleUpperCase(locale)&&c!==c.toLocaleLowerCase(locale)?c.toLocaleLowerCase(locale):c===c.toLocaleLowerCase(locale)&&c!==c.toLocaleUpperCase(locale)?c.toLocaleUpperCase(locale):c).join('');
 if(op==='sponge'){let cap=false;return[...v].map(c=>{if(!/\p{L}/u.test(c))return c;cap=!cap;return cap?c.toLocaleUpperCase(locale):c.toLocaleLowerCase(locale)}).join('')}
 const words=v.trim().split(/[^\p{L}\p{N}]+/u).filter(Boolean).map(x=>x.toLocaleLowerCase(locale));const C=x=>x?[...x][0].toLocaleUpperCase(locale)+[...x].slice(1).join(''):x;
 if(op==='snake')return words.join('_');if(op==='kebab')return words.join('-');if(op==='camel')return words.map((x,i)=>i?C(x):x).join('');if(op==='pascal')return words.map(C).join('');return v;
}
export const cloneDoc=d=>({text:d.text,marks:d.marks.map(x=>({...x})),paragraphs:d.paragraphs.map(x=>({...x}))});
export function normSel(s,len){const a=Math.max(0,Math.min(s.start,len)),b=Math.max(0,Math.min(s.end,len));return{start:Math.min(a,b),end:Math.max(a,b)}}
export function selectedOrAll(s,len){const r=normSel(s,len);return r.start===r.end?{start:0,end:len}:r}
const overlap=(a,b)=>a.start<b.end&&b.start<a.end;
function adjust(ranges,a,b,newEnd){const delta=newEnd-b;return ranges.map(r=>{if(r.end<=a)return{...r};if(r.start>=b)return{...r,start:r.start+delta,end:r.end+delta};const start=r.start<a?r.start:a,end=r.end>b?r.end+delta:newEnd;return{...r,start,end:Math.max(start,end)}}).filter(r=>r.end>r.start)}
export function replaceRange(doc,sel,repl){const r=normSel(sel,doc.text.length),e=r.start+repl.length;return{doc:{text:doc.text.slice(0,r.start)+repl+doc.text.slice(r.end),marks:adjust(doc.marks,r.start,r.end,e),paragraphs:adjust(doc.paragraphs,r.start,r.end,e)},selection:{start:r.start,end:e}}}
export function typingUpdate(doc,text){if(doc.text===text)return doc;let p=0;while(p<Math.min(doc.text.length,text.length)&&doc.text[p]===text[p])p++;let s=0,lim=Math.min(doc.text.length,text.length)-p;while(s<lim&&doc.text[doc.text.length-1-s]===text[text.length-1-s])s++;return{text,marks:adjust(doc.marks,p,doc.text.length-s,text.length-s),paragraphs:adjust(doc.paragraphs,p,doc.text.length-s,text.length-s)}}
export function addMark(doc,sel,patch){const r=selectedOrAll(sel,doc.text.length);if(r.start===r.end)return doc;const same=(m)=>('color'in patch&&m.color)||('background'in patch&&m.background)||('underline'in patch&&m.underline)||('underlineColor'in patch&&m.underlineColor);return{...doc,marks:[...doc.marks.filter(m=>!same(m)||!overlap(m,r)),{id:crypto.randomUUID?.()??String(Date.now()),start:r.start,end:r.end,...patch}]}}
export function clearMarks(doc,sel){const r=selectedOrAll(sel,doc.text.length);return{...doc,marks:doc.marks.filter(m=>!overlap(m,r))}}
export function paragraphRange(text,sel){const r=selectedOrAll(sel,text.length);let a=r.start,b=r.end;while(a>0&&text[a-1]!=='\n')a--;while(b<text.length&&text[b]!=='\n')b++;if(b<text.length)b++;return{start:a,end:b}}
export function setAlign(doc,sel,align){const r=paragraphRange(doc.text,sel),p=doc.paragraphs.filter(x=>!overlap(x,r));return align==='left'?{...doc,paragraphs:p}:{...doc,paragraphs:[...p,{id:String(Date.now()),start:r.start,end:r.end,align}]}}
export function alignAt(doc,i){return[...doc.paragraphs].reverse().find(x=>x.start<=i&&i<x.end)?.align??'left'}
const letters=n=>{let r='';for(let x=Math.max(1,n);x>0;){x--;r=String.fromCharCode(65+x%26)+r;x=Math.floor(x/26)}return r};
const roman=n=>{let x=Math.max(1,Math.min(n,3999)),r='';for(const [v,s] of [[1000,'M'],[900,'CM'],[500,'D'],[400,'CD'],[100,'C'],[90,'XC'],[50,'L'],[40,'XL'],[10,'X'],[9,'IX'],[5,'V'],[4,'IV'],[1,'I']])while(x>=v){r+=s;x-=v}return r};
export function applyList(doc,sel,op){const r=paragraphRange(doc.text,sel),src=doc.text.slice(r.start,r.end),trail=src.endsWith('\n'),lines=src.replace(/\n$/,'').split('\n');const prefix=(i)=>op==='number'?`${i}. `:op==='letter'?`${letters(i)}. `:op==='roman'?`${roman(i)}. `:({bullet:'• ',dash:'– ',check:'✓ ',arrow:'➜ ',star:'★ ',diamond:'◆ '}[op]??'');const out=lines.map((line,i)=>{if(!line.trim())return line;const ind=line.match(/^\s*/u)?.[0]??'',body=line.slice(ind.length).replace(/^(?:(?:\d+|[A-Za-z]|[IVXLCDMivxlcdm]+)[.)]|[•◦▪▫●○◆◇★☆✓✔➜→–—-])\s+/u,'');return ind+(op==='remove'?'':prefix(i+1))+body}).join('\n')+(trail?'\n':'');return replaceRange(doc,r,out)}
export function plainText(doc){const u=doc.marks.filter(m=>m.underline||m.underlineColor);if(!u.length)return doc.text;let out='';for(let i=0;i<doc.text.length;){const cp=doc.text.codePointAt(i),c=String.fromCodePoint(cp),l=c.length;out+=c;if(u.some(m=>m.start<i+l&&i<m.end)&&c!=='\n'&&c!=='\r'&&!/\p{M}/u.test(c))out+='\u0332';i+=l}return out}
export function segments(doc,start,end){const pts=new Set([start,end]);doc.marks.forEach(m=>{if(overlap(m,{start,end})){pts.add(Math.max(start,m.start));pts.add(Math.min(end,m.end))}});const p=[...pts].sort((a,b)=>a-b),out=[];for(let i=0;i<p.length-1;i++){const a=p[i],b=p[i+1];if(b<=a)continue;const style={};doc.marks.filter(m=>m.start<b&&a<m.end).forEach(m=>{if(m.color)style.color=m.color;if(m.background)style.backgroundColor=m.background;if(m.underline||m.underlineColor){style.textDecorationLine='underline';style.textDecorationColor=m.underlineColor??'currentColor';style.textDecorationThickness='2px';style.textUnderlineOffset='3px'}});out.push({text:doc.text.slice(a,b),style})}return out}
