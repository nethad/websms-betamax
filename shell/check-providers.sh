#!/bin/sh





providers=(
# fully working
www.voipcheap.com 
www.voipbuster.com 
www.12voip.com 
www.dialnow.com 
www.freecall.com 
www.justvoip.com 
www.lowratevoip.com 
www.smsdiscount.com 
www.voipwise.com 
www.intervoip.com 
www.poivy.com 
www.voipbusterpro.com 
www.voipstunt.com 
www.webcalldirect.com 
www.budgetsip.com 
www.powervoip.com 
www.voicetrading.com 
www.voipblast.com 
# only messages
www.jumblo.com 
www.rynga.com 
www.telbo.com 
www.actionvoip.com 
www.smartvoip.com 
www.easyvoip.com 
www.voipmove.com 
www.internetcalls.com 
www.sipdiscount.com 
www.voipdiscount.com 
www.voipraider.com 
www.freevoipdeal.com 
www.cosmovoip.com 
www.budgetvoipcall.com 
www.cheapbuzzer.com 
www.callpirates.com 
www.cheapvoipcall.com 
www.dialcheap.com 
www.discountcalling.com 
www.frynga.com 
www.globalfreecall.com 
www.hotvoip.com 
www.megavoip.com 
www.pennyconnect.com 
www.rebvoice.com 
www.stuntcalls.com 
www.voipblazer.com 
www.voipcaptain.com 
www.voipchief.com 
www.voipjumper.com 
www.voipsmash.com
#not working
www.netappel.com 
www.voiparound.com 
www.sparvoip.com 
www.voiphit.com 
www.voipalot.com 
www.fastvoip.com )


echo "Check providers..."

for provider in ${providers[*]}
do
  echo "Check $provider"
  curl --silent --head https://$provider/myaccount/getbalance.php|grep HTTP
  curl --silent --head https://$provider/myaccount/sendsms.php|grep HTTP
done
