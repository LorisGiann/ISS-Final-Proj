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
	
	fun nextPos(CURRPOS:ws.Position) : ws.Position = when(CURRPOS){
		   Position.HOME -> Position.INDOOR
		   Position.INDOOR -> Position.PLASTICBOX
		   Position.PLASTICBOX -> Position.GLASSBOX
		   Position.GLASSBOX -> Position.HOME
   }
	
	fun prevPos(CURRPOS:ws.Position) : ws.Position = when(CURRPOS){
		   Position.HOME -> Position.GLASSBOX
		   Position.INDOOR -> Position.HOME
		   Position.PLASTICBOX -> Position.INDOOR
		   Position.GLASSBOX -> Position.PLASTICBOX
   }
}