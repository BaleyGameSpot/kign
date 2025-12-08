-- Add Personal Driver category to vehicle_category table
-- This category will appear in the ride categories

INSERT INTO vehicle_category VALUES(
"355",
-- vCategory in multiple languages
"Personal Driver","私人司机","Osobní řidič","Personal Driver","Personlig chauffør","Chauffeur personnel","נהג אישי","निजी ड्राइवर","Osobisty kierowca","Личный водитель","Kişisel Şoför","개인 운전사","Personal na Driver","Pemandu Peribadi","Șofer personal","Motorista Pessoal","Προσωπικός οδηγός","Persoonlijke chauffeur","سائق شخصي","Autista personale","Personlig förare","Conductor personal","Personlig sjåfør","Persönlicher Fahrer","คนขับส่วนตัว",
-- vCategoryTitle in multiple languages (empty for now)
"","","","","","","","","","","","","","","","","","","","","","","","","","",
-- tCategoryDesc in multiple languages (empty for now)
"","","","","","","","","","","","","","","","","","","","","","","","","","",
-- iParentId
"0",
-- vLogo, vLogo1, vLogo2
"ic_personal_driver_EN.png","ic_personal_driver_EN.png","",
-- vHomepageLogo
"image_personal_driver.svg",
-- ePriceType
"Service",
-- eBeforeUpload, eAfterUpload
"","",
-- iDisplayOrder
"2",
-- eStatus
"Active",
-- eShowType
"Icon",
-- eMaterialCommision
"No",
-- vBannerImage
"personal_driver_banner.jpg",
-- eCatType (Important: This is set to "Ride" for ride categories)
"Ride",
-- eSubCatType
"",
-- eFor
"RideCategory",
-- eDeliveryType
"",
-- iServiceId
"0",
-- tBannerButtonText (JSON with multiple languages)
"{\"tBannerButtonText_EN\":\"Book Now\",\"tBannerButtonText_HI\":\"अभी बुक करें\",\"tBannerButtonText_AR\":\"احجز الآن\",\"tBannerButtonText_FR\":\"Réserver maintenant\",\"tBannerButtonText_ES\":\"Reservar ahora\",\"tBannerButtonText_DE\":\"Jetzt buchen\",\"tBannerButtonText_IT\":\"Prenota ora\",\"tBannerButtonText_PT\":\"Reserve agora\",\"tBannerButtonText_RU\":\"Забронировать\",\"tBannerButtonText_ZHCN\":\"立即预订\"}",
-- eDetailPageView
"",
-- fCommision
"0.00",
-- fWaitingFees
"0.00",
-- iWaitingFeeTimeLimit
"0",
-- fCancellationFare
"0.00",
-- iCancellationTimeLimit
"0",
-- iMasterVehicleCategoryId
"0",
-- iDisplayOrderHomepage
"2",
-- lCatDescHomepage (JSON - empty for now)
"{\"lCatDescHomepage_EN\":\"\",\"lCatDescHomepage_HI\":\"\",\"lCatDescHomepage_AR\":\"\"}",
-- vCatDescbtnHomepage (JSON - empty)
"{\"vCatDescbtnHomepage_EN\":\"\",\"vCatDescbtnHomepage_HI\":\"\",\"vCatDescbtnHomepage_AR\":\"\"}",
-- vCatNameHomepage (JSON - empty)
"{\"vCatNameHomepage_EN\":\"\",\"vCatNameHomepage_HI\":\"\",\"vCatNameHomepage_AR\":\"\"}",
-- vCatSloganHomepage (JSON - empty)
"{\"vCatSloganHomepage_EN\":\"\",\"vCatSloganHomepage_HI\":\"\",\"vCatSloganHomepage_AR\":\"\"}",
-- vCatTitleHomepage (JSON - empty)
"{\"vCatTitleHomepage_EN\":\"\",\"vCatTitleHomepage_HI\":\"\",\"vCatTitleHomepage_AR\":\"\"}",
-- vHomepageBanner
"personal_driver_home_banner.jpg",
-- vServiceCatTitleHomepage (JSON with titles and descriptions)
"{\"vServiceCatTitleHomepage_EN\":\"Personal Driver\",\"vServiceCatTitleHomepage_HI\":\"निजी ड्राइवर\",\"vServiceCatTitleHomepage_AR\":\"سائق شخصي\",\"vServiceCatTitleHomepage_FR\":\"Chauffeur Personnel\",\"vServiceCatTitleHomepage_ES\":\"Conductor Personal\",\"vServiceCatTitleHomepage_DE\":\"Persönlicher Fahrer\",\"vServiceCatSubTitleHomepage_EN\":\"Hire a professional personal driver for your journeys. Whether it's for daily commute, long trips, or special occasions, get a dedicated driver at your service.\",\"vServiceCatSubTitleHomepage_HI\":\"अपनी यात्राओं के लिए एक पेशेवर निजी ड्राइवर को किराए पर लें। चाहे वह दैनिक यात्रा के लिए हो, लंबी यात्रा के लिए या विशेष अवसरों के लिए, अपनी सेवा में एक समर्पित ड्राइवर प्राप्त करें।\",\"vServiceCatSubTitleHomepage_AR\":\"استأجر سائقًا شخصيًا محترفًا لرحلاتك. سواء كان ذلك للتنقل اليومي أو الرحلات الطويلة أو المناسبات الخاصة ، احصل على سائق مخصص في خدمتك.\"}",
-- vServiceHomepageBanner
"personal_driver_service_banner.jpg",
-- eCatViewType
"Icon",
-- tListDescription (JSON - empty)
"{\"tListDescription_EN\":\"\",\"tListDescription_HI\":\"\",\"tListDescription_AR\":\"\"}",
-- vListLogo, vListLogo1, vListLogo2, vListLogo3
"","ic_personal_driver_list.png","ic_personal_driver_list2.png","",
-- eOTPCodeEnable
"No",
-- ePromoteBanner
"No",
-- vPromoteBannerImage
"",
-- tPromoteBannerTitle (JSON - empty)
"{\"tPromoteBannerTitle_EN\":\"\",\"tPromoteBannerTitle_HI\":\"\",\"tPromoteBannerTitle_AR\":\"\"}",
-- vHomepageLogoOurServices
"personal_driver_our_services.png",
-- eVideoConsultEnable
"No",
-- eVideoConsultServiceCharge
"0.00",
-- eVideoServiceDescription
"",
-- fCommissionVideoConsult
"0.00",
-- vIconDetails
"",
-- eForMedicalService
"No",
-- tMedicalServiceInfo
"",
-- vServiceImage
"",
-- iDisplayOrderVC
"0"
);

-- Note: You'll need to add the actual image files to your server's upload directory
-- Default placeholder images are specified above
