/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/wzy/Documents/tb/wzy/workspace_AS/jiayue_1.4.9/src/com/jiayue/service/IMusicPlayerService.aidl
 */
package com.jiayue.service;
/**
 * 播放歌曲
 * @author Administrator
 *
 */
public interface IMusicPlayerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jiayue.service.IMusicPlayerService
{
private static final java.lang.String DESCRIPTOR = "com.jiayue.service.IMusicPlayerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jiayue.service.IMusicPlayerService interface,
 * generating a proxy if needed.
 */
public static com.jiayue.service.IMusicPlayerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jiayue.service.IMusicPlayerService))) {
return ((com.jiayue.service.IMusicPlayerService)iin);
}
return new com.jiayue.service.IMusicPlayerService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getAudioPath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAudioPath();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_notifyChange:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.notifyChange(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setAudioList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.jiayue.dto.base.AudioItem> _arg0;
_arg0 = data.createTypedArrayList(com.jiayue.dto.base.AudioItem.CREATOR);
this.setAudioList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getAudioList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.jiayue.dto.base.AudioItem> _result = this.getAudioList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getAudioItem:
{
data.enforceInterface(DESCRIPTOR);
com.jiayue.dto.base.AudioItem _result = this.getAudioItem();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getAudioPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAudioPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
this.play();
reply.writeNoException();
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_openAudio:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.openAudio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_seeKTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seeKTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getAudioName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAudioName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getAllAudio:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.getAllAudio(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getArtistName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getArtistName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getDuration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDuration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentPositon:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentPositon();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setPlayMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPlayMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPlayMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPlayMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_next:
{
data.enforceInterface(DESCRIPTOR);
this.next();
reply.writeNoException();
return true;
}
case TRANSACTION_pre:
{
data.enforceInterface(DESCRIPTOR);
this.pre();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jiayue.service.IMusicPlayerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String getAudioPath() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioPath, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void notifyChange(java.lang.String notify) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(notify);
mRemote.transact(Stub.TRANSACTION_notifyChange, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setAudioList(java.util.List<com.jiayue.dto.base.AudioItem> audioLists) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(audioLists);
mRemote.transact(Stub.TRANSACTION_setAudioList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List<com.jiayue.dto.base.AudioItem> getAudioList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.jiayue.dto.base.AudioItem> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.jiayue.dto.base.AudioItem.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiayue.dto.base.AudioItem getAudioItem() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiayue.dto.base.AudioItem _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioItem, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiayue.dto.base.AudioItem.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getAudioPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 播放音乐
	 */
@Override public void play() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 暂停音乐
	 */
@Override public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 根据位置打开一个音频
	 * @param position
	 */
@Override public void openAudio(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_openAudio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 拖动视频
	 * @param position
	 */
@Override public void seeKTo(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_seeKTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 得到音频的名称
	 * @return
	 */
@Override public java.lang.String getAudioName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAudioName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void getAllAudio(java.lang.String path) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
mRemote.transact(Stub.TRANSACTION_getAllAudio, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 得到艺术家名字
	 * @return
	 */
@Override public java.lang.String getArtistName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getArtistName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getDuration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDuration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getCurrentPositon() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentPositon, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 设置模式
	 * @param playmode
	 */
@Override public void setPlayMode(int playmode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playmode);
mRemote.transact(Stub.TRANSACTION_setPlayMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getPlayMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * 播放下一个音频
	 */
@Override public void next() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_next, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * 播放上一个音频
	 */
@Override public void pre() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pre, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getAudioPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_notifyChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setAudioList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getAudioList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getAudioItem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getAudioPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_openAudio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_seeKTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getAudioName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getAllAudio = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getArtistName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getCurrentPositon = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_setPlayMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getPlayMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_next = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_pre = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
}
public java.lang.String getAudioPath() throws android.os.RemoteException;
public void notifyChange(java.lang.String notify) throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public void setAudioList(java.util.List<com.jiayue.dto.base.AudioItem> audioLists) throws android.os.RemoteException;
public java.util.List<com.jiayue.dto.base.AudioItem> getAudioList() throws android.os.RemoteException;
public com.jiayue.dto.base.AudioItem getAudioItem() throws android.os.RemoteException;
public int getAudioPosition() throws android.os.RemoteException;
/**
	 * 播放音乐
	 */
public void play() throws android.os.RemoteException;
/**
	 * 暂停音乐
	 */
public void pause() throws android.os.RemoteException;
/**
	 * 根据位置打开一个音频
	 * @param position
	 */
public void openAudio(int position) throws android.os.RemoteException;
/**
	 * 拖动视频
	 * @param position
	 */
public void seeKTo(int position) throws android.os.RemoteException;
/**
	 * 得到音频的名称
	 * @return
	 */
public java.lang.String getAudioName() throws android.os.RemoteException;
public void getAllAudio(java.lang.String path) throws android.os.RemoteException;
/**
	 * 得到艺术家名字
	 * @return
	 */
public java.lang.String getArtistName() throws android.os.RemoteException;
public int getDuration() throws android.os.RemoteException;
public int getCurrentPositon() throws android.os.RemoteException;
/**
	 * 设置模式
	 * @param playmode
	 */
public void setPlayMode(int playmode) throws android.os.RemoteException;
public int getPlayMode() throws android.os.RemoteException;
/**
	 * 播放下一个音频
	 */
public void next() throws android.os.RemoteException;
/**
	 * 播放上一个音频
	 */
public void pre() throws android.os.RemoteException;
}
