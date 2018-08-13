package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Source {
	private int sourceId;
	
	public Source(){
		sourceId = AL10.alGenSources();
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 2.5f);
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, 6);
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE , 50);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0,0,0);
	}
	public Source(float rolloff, float referenceDistance, float maxDistance){
		sourceId = AL10.alGenSources();
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rolloff);
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE , maxDistance);
		AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
		AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
		AL10.alSource3f(sourceId, AL10.AL_POSITION, 0,0,0);
	}
	public void play(int buffer){
		stop();
		AL10.alSourcei(sourceId	, AL10.AL_BUFFER, buffer);
		continuePlaying();
	}
	
	public void delete(){
		stop();
		AL10.alDeleteSources(sourceId);
	}
	public void setVolume(float volume){
		AL10.alSourcef(sourceId, AL10.AL_PITCH, volume);
	}
	
	public void setPitch(float pitch){
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
		
	}
	public void pause(){
		AL10.alSourcePause(sourceId);
	}
	public void stop(){
		AL10.alSourceStop(sourceId);
	}
	
	public void continuePlaying(){
		AL10.alSourcePlay(sourceId);
	}
	
	public void setLooping(boolean loop){
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	public boolean isPlaying(){
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public void setPosition(float x, float y, float z){
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
	}
	public void setPosition(Vector3f pos){
		AL10.alSource3f(sourceId, AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}
	public void setVelocity(float x, float y, float z){
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
	}
	public void setVelocity(Vector3f pos){
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, pos.x, pos.y, pos.z);
	}
}