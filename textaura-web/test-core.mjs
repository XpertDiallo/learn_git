import assert from 'node:assert/strict';
import {unicodeStyle,caseTransform,applyList,replaceRange,addMark,plainText} from './core.js';
assert.equal(unicodeStyle('Abc 12','bold'),'𝗔𝗯𝗰 𝟭𝟮');
assert.equal(caseTransform('bonjour. SALUT!','sentence','fr'),'Bonjour. Salut!');
assert.equal(caseTransform('Smileys et émotions','snake','fr'),'smileys_et_émotions');
let d={text:'Un\nDeux',marks:[],paragraphs:[]};d=applyList(d,{start:0,end:d.text.length},'number').doc;assert.equal(d.text,'1. Un\n2. Deux');
let x={text:'abc',marks:[],paragraphs:[]};x=addMark(x,{start:0,end:3},{underline:true});assert.equal(plainText(x),'a̲b̲c̲');
assert.equal(replaceRange(x,{start:1,end:2},'XYZ').doc.text,'aXYZc');
console.log('TextAura core tests: OK');
