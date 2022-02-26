
package com.jzy.ai.msg;

/**处理电报接口<br>
 * Any object implementing the {@code Telegraph} interface can act as the sender
 * or the receiver of a {@link Telegram}.
 * 任何实现Telegraph接口的对象，都可以作为电报的发送方或者接收方
 * @author davebaol
 */
public interface Telegraph {

	/**
	 * Handles the telegram just received.
	 * 
	 * @param msg
	 *            The telegram
	 * @return {@code true} if the telegram has been successfully handled;
	 *         {@code false} otherwise.
	 */
	public boolean handleMessage(Telegram msg);

}
