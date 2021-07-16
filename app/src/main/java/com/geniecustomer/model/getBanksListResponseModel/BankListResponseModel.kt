package genie.service.provider.activities.addBankDetail.getBanksListResponseModel

data class BankListResponseModel(
    val `data`: Data = Data(),
    val message: String = "",
    val success: Boolean = false
)