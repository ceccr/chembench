
neonBaseColor = "white";
neonColor = "#F86217";
num = 0;
num2 = 0;
num3 = 0;
num4 = neonColor;
function startNeon() {
message = neon.innerText;
neon.innerText = "";
for(i = 0; i != message.length; i++) {
neon.innerHTML += "<span id=\"neond\" style=\"color:"+neonBaseColor+"\">"+message.charAt(i)+"<\/span>"};
neon2();
}
function neon2() {
if(num != message.length) {
document.all.neond[num].style.color = neonColor;
num++;
setTimeout("neon2()", 100);
}
else {
num = 0;
num2 = message.length;
setTimeout("neon4onev()", 2000);
   }
}
function neon4onev() {
document.all.neond[num].style.color = neonBaseColor;
document.all.neond[num2-1].style.color = neonBaseColor;
if(Math.floor(message.length / 2) + 1 != num2) {
num++;
num2--;
setTimeout("neon4onev()", 50);
}
else {
setTimeout("neon5()", 50);
   }
}
function neon5() {
if(num3 != message.length && num3 != message.length+1) {
document.all.neond[num3].style.color = neonColor;
num3 = num3 + 2;
setTimeout("neon5()",100);
}
else {
setTimeout("neon52()", 50);
   }
}
function neon52() {
if(num3 == message.length) {
num3++;
neon52a();
}
else {
num3--;
neon52a();
   }
}
function neon52a() {
if(num3 != 1) {
num3 = num3 - 2;
document.all.neond[num3].style.color = neonColor;
setTimeout("neon52a()", 100);
}
else {
if(num4 == neonColor) {
num3 = 0;
neonColor = neonBaseColor;
setTimeout("neon5()", 2000);
}
else {
neonColor = num4;
num3 = 0;
setTimeout("neon4onev2()", 50);
      }
   }
}
function neon4onev2() {
document.all.neond[num].style.color = neonColor;
document.all.neond[num2 - 1].style.color = neonColor;
if(message.length != num2) {
num--;
num2++;
setTimeout("neon4onev2()", 50);
}
else {
num = 0;
num2 = 0;
setTimeout("neon3()", 2000);
   }
}
function neon3() {
if(num != message.length) {
document.all.neond[num].style.color = neonBaseColor;
num++;
setTimeout("neon3()", 100);
}
else {
num = 0;
neon2();
   }
}

