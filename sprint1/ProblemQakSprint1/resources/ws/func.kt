package ws

object func {
    var contPB : Float = 0F
    var contGB : Float = 0F

    fun checkdepositpossible(Material:ws.Material, TruckLoad:Float) : Boolean {
        return (Material==ws.Material.PLASTIC && TruckLoad+contPB<=ws.const.MAXPB)
                || (Material==ws.Material.GLASS && TruckLoad+contGB<=ws.const.MAXGB);
    }

    fun updateDeposit(Material:ws.Material, TruckLoad:Float) : Unit {
        when(Material){
            ws.Material.PLASTIC -> contPB+=TruckLoad
            ws.Material.GLASS -> contGB+=TruckLoad
            else -> {
                print("ERRORE MATERIALE")
            }
        }
    }
}