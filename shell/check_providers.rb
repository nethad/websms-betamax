#!/usr/bin/ruby

require 'net/https'
require 'uri'

def check_code_and_print(uri)
	uri = URI.parse(uri)
	http = Net::HTTP.new(uri.host, uri.port)
	http.use_ssl = true
	http.verify_mode = OpenSSL::SSL::VERIFY_NONE
	request = Net::HTTP::Get.new(uri.request_uri)
	begin
		response = http.request(request)
		if response.code =~ /200/
			print " <font color=\"green\">Yes</font> ||"
		elsif response.code =~ /3\d\d/
			print " <font color=\"orange\">Maybe (#{response.code})</font> ||"
		else
			print " <font color=\"red\">No (#{response.code})</font> ||"
		end
	rescue Exception => msg
		print " Could not check (#{msg}) ||"
	end

end

providers = %w(
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
www.voiparound.com 

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

www.netappel.com 
)

puts "|| *Provider* || *Balance Checks* || *Sending Messages* ||"

providers.each do |provider|
	print "|| http://#{provider} ||"
	check_code_and_print("https://#{provider}/myaccount/getbalance.php")
	check_code_and_print("https://#{provider}/myaccount/sendsms.php")
	puts ""
end
