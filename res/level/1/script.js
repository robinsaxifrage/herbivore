js_function generate(){
	var level = new Level();
	var cabinDoor = EntityFactory.loadEntity(Resource.getCustomizableResource("prefab/door/woodenDoor.entity"), null);
	var outsideDoor = EntityFactory.loadEntity(Resource.getCustomizableResource("prefab/door/woodenDoor.entity"), null);
	var cabin = $this.invoke("generateCabin", level, cabinDoor);
	var outside = $this.invoke("generateOutside", level, outsideDoor);
	cabinDoor.linkWith(outsideDoor);
	outsideDoor.linkWith(cabinDoor);
	level.addSpace(cabin);
	level.addSpace(outside);
	return level;
}

js_function generateCabin(level, door){
	var resizeRatio = BuildInfo.getTextureResizeRatio();
	var width = 98*resizeRatio;
	var space = new Space("farmer's cabin", level, width, false);
	space.addAmbience(Resource.getCustomizableResource("sound/ambience/wasteland.wav"), 0.3);
	space.addAmbience(Resource.getCustomizableResource("sound/ambience/indoor.wav"), 0.7);
	var ground = Renderer.get().getWindowHeight()/2 + (45*resizeRatio/2);
	space.add("texture/unnatural/cabinInterior.entity", 0, ground);
	space.add("farmer.entity", 200, ground);
	space.add("player.entity", 400, ground);
	space.add("ground/planks.entity", 0, ground, width);
	space.add(door, 10*resizeRatio, ground);
	return space;
}

js_function generateOutside(level, door){
	var resizeRatio = BuildInfo.getTextureResizeRatio();
	var space = new Space("outside", level, 5000, true);
	space.addAmbience(Resource.getCustomizableResource("sound/ambience/wasteland.wav"), 0.5);
	space.addAmbience(Resource.getCustomizableResource("sound/ambience/outdoor.wav"), 0.7);
	space.addAmbience(Resource.getCustomizableResource("sound/ambience/birds.wav"), 0.7);
	var grassWidth = 50*resizeRatio;
	var grassSlopeHeight = 30*resizeRatio;
	var ground = (Renderer.get().getWindowHeight()/4)*3;
	var bottomGround = ground;
	var topGround;
	var topWidth = grassWidth*5;
	space.add("texture/natural/grassCliff.entity", 0, ground);
	space.add(door, 150, ground);
	space.add("texture/unnatural/cabinExterior.entity", 150 - 10*BuildInfo.getTextureResizeRatio(), ground);
	space.add("texture/natural/mediumTree.entity", 700, ground, "front", 0);
	space.add("item/food/freshApple.entity", 750, ground);
	space.add("texture/natural/mediumTree.entity", 800, ground, "back", 1);
	space.add("texture/natural/smallTree.entity", 950, ground, "front", 1);
	space.add("texture/natural/mediumTree.entity", 1100, ground, "front", 1);
	space.add("ground/grassSlope.entity", 1500 - grassWidth, ground, "false");
	ground -= grassSlopeHeight - 4*resizeRatio;
	topGround = ground;
	space.add("item/weapon/shotgun.entity", 1350, ground);
	space.add("item/weapon/shotgunMag.entity", 1200, ground);
	space.add("item/weapon/spear.entity", 1250, ground);
	space.add("vagrant.entity", 1500, ground);
	space.add("vagrant.entity", 1700, ground);
	var winLever = EntityFactory.loadEntity(Resource.getCustomizableResource("prefab/misc/lever.entity"), null);
	winLever.addOpBinding(new OperationBinding(Resource.getCustomizableResource("prefab/winGameBinding.op")));
	space.add(winLever, 1700, ground);
	space.add("vagrant.entity", 1850, ground);
	ground = bottomGround;
	space.add("ground/grassSlope.entity", 1500 + topWidth, ground, "true");
	space.add("vagrant.entity", 3500, ground);
	$this.invoke("generateGrassGround", space, 0, bottomGround, 1500 - grassWidth);
	$this.invoke("generateGrassGround", space, 1500, topGround, topWidth);
	$this.invoke("generateGrassGround", space, 1500 + grassWidth*6, bottomGround, 3500 - topWidth);
	$this.invoke("generateDirt", space, 0, bottomGround, 1500);
	$this.invoke("generateDirt", space, 1500, topGround, topWidth);
	$this.invoke("generateDirt", space, 1500 + topWidth, bottomGround, 3500 - topWidth);
	return space;
}

js_function generateGrassGround(space, x, y, width){
	var resizeRatio = BuildInfo.getTextureResizeRatio();
	var cellWidth = 50*resizeRatio;
	var xPos = 0;
	while (xPos < width){
		space.add("texture/natural/grassBlades.entity", x + xPos, y);
		xPos += cellWidth;
	}
	space.add("ground/grass.entity", x, y, width, 50);
}

js_function generateDirt(space, x, y, width){
	var resizeRatio = BuildInfo.getTextureResizeRatio();
	var cellWidth = 50*resizeRatio;
	var dirtCellHeight = 8*resizeRatio;
	var xPos = 0;
	while (xPos < width){
		var yPos = 0;
		while (yPos < Renderer.get().getWindowHeight() - y){
			space.add("texture/natural/dirt.entity", x + xPos, y + yPos);
			yPos += dirtCellHeight;
		}
		xPos += cellWidth;
	}
}