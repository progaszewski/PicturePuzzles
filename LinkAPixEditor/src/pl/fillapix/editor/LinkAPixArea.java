package pl.fillapix.editor;

public class LinkAPixArea {

	public byte[][] area;

	public void setArea(int x, int y) {
		if (this.area == null) {
			this.area = new byte[y][x];

			for (int i = 0; i < this.area.length; i++) {
				for (int j = 0; j < this.area[i].length; j++) {
					this.area[i][j] = -1;
				}
			}
		} else {
			byte[][] tmpArea = this.area.clone();

			this.area = new byte[y][x];

			for (int i = 0; i < this.area.length; i++) {
				for (int j = 0; j < this.area[i].length; j++) {

					if (i >= tmpArea.length || j >= tmpArea[0].length) {
						this.area[i][j] = -1;
					} else {
						this.area[i][j] = tmpArea[i][j];
					}

				}
			}
		}
	}
}
