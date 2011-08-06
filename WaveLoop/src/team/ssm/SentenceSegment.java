package team.ssm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SentenceSegment implements Serializable {

	private static final long serialVersionUID = -6112331169298149437L;
	public boolean isSilence;
	public int startOffset;
	public int size;
	
	
	public SentenceSegment( boolean isSilence, int startOffset, int size )
	{
		this.isSilence = isSilence;
		this.startOffset = startOffset;
		this.size = size;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeBoolean(isSilence);
		out.writeInt(startOffset);
		out.writeInt(size);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		isSilence = in.readBoolean();
		startOffset = in.readInt();
		size = in.readInt();
	}
	
	
}
