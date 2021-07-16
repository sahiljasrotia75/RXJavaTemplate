package genie.service.provider.activities.addBankDetail.getBanksListResponseModel

data class Data(
    val _id: String = "",
    val banks: List<Bank> = listOf(),
    val documents: ArrayList<Document> = ArrayList(),
    val tax: Int = 0
)