package cl.niclabs.skandium.gcm.taskheader;


public class NullTaskHeader extends AbstractTaskHeader {

	private static final long serialVersionUID = 1L;

	public NullTaskHeader() {
		
		this.originId = -1;
		this.data = null;
		this.stack = null;
	}

}
