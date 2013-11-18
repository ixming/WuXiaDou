package com.wuxiadou.network;

import java.util.Vector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import com.wuxiadou.network.listener.OnLoadListener;
import com.wuxiadou.network.utils.NetWorkUtils;

public class LoadHelper
{
    private static final String TAG = LoadHelper.class.getSimpleName();
    
    private boolean isRun = true;
    private boolean exceBool = true;

    private static LoadHelper loadHelper = null;
    private static final int QUEUE_COUNT = 50;
    private static Vector<ReqBean> queue = new Vector<ReqBean>();

    private volatile static int runCount = 1;
    
    private static volatile int CONNECTCOUNT = 2;
    
    private static Vector<ReqBean> runningQueue = new Vector<ReqBean>(QUEUE_COUNT);
    
    
    private Object syncToken = new Object();
    
    private LoadHelper()
    {
        stopNet();
        startNet();
        initExceReq();
    }

    public synchronized static LoadHelper getInstance()
    {
        if (loadHelper == null)
        {
            loadHelper = new LoadHelper();
        }
        return loadHelper;
    }

    private void initExceReq()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (isRun)
                {
                    try
                    {
                        if ((queue == null) || queue.isEmpty())
                        {
                            synchronized (syncToken)
                            {
                                Log.w(TAG, "syncToken.wait()");
                                syncToken.wait();
                            }
                        }
                        else
                        {
                        	 if (runCount < CONNECTCOUNT)
                             {
                                 if (queue != null && !queue.isEmpty())
                                 {
                                     runCount = runCount + 1;
                                     ReqBean bean = queue.remove(0);
                                     if(bean.getContext() != null)
                                     {
                                    	 String netType = NetworkManager.getNetWorkType((ConnectivityManager) bean.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
                                         if(NetWorkUtils.WIFI_STATE.equalsIgnoreCase(netType))
                                         {
                                        	 CONNECTCOUNT = 8;
                                         }
                                     }
                                     runningQueue.add(bean);
                                     exceReq(bean);
                                 }
                             }
                            Log.w(TAG, "Sleep 1 second...");
                            Thread.sleep(1000l);
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public synchronized void addRequest(ReqBean bean)
    {
        if (null == bean)
        {
            return;
        }
        try
        {
    	    if(runningQueue.contains(bean) || queue.contains(bean))
    	    {
    		   return;
    	    }
            queue.add(bean);

            synchronized (syncToken)
            {
               syncToken.notify();
                
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void exceReq(final ReqBean bean)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpRes res = null;
                try
                {
                    // 增加封装psot请求的请求体 并在sheal中得到最新的token值
                    if (bean != null&&NetWorkUtils.isNotNull(bean.getUrl()))
                    {
                    	String reqType = bean.getReqType();
                        if (SendRequestUtil.POST.equalsIgnoreCase(reqType))
                        {
                            res = HttpClientUtil.proxyHttpPost(
                                    bean.getContext(),
                                    bean.getUrl(),bean.getJson(),bean.getSign());
                            
                        } else if (SendRequestUtil.POST_FILE.equalsIgnoreCase(reqType)) {
                        	res = HttpClientUtil.proxyHttpPostFile(
                                    bean.getContext(),
                                    bean.getUrl(),bean.getJson(),bean.getSign(), bean.getReqExtras());
                        }else if(SendRequestUtil.PUT_FILE.equalsIgnoreCase(reqType))
                        {
                        	res = HttpClientUtil.proxyHttpPutFile(
                                    bean.getContext(),
                                    bean.getUrl(),bean.getJson(),bean.getSign(), bean.getReqExtras());
                        }
                        else if(bean.getReqType().equalsIgnoreCase(SendRequestUtil.GET))
                        {
                            res = HttpClientUtil.proxyHttpGet(bean.getContext(), bean.getUrl(),
                            		bean.getJson(),
                            		bean.getSign());
                        }else if(bean.getReqType().equalsIgnoreCase(SendRequestUtil.DELETE))
                        {
                        	res=HttpClientUtil.proxyHttpDelete(bean.getContext(),bean.getUrl(),
                            		bean.getJson(),
                            		bean.getSign());
                        }else if(bean.getReqType().equalsIgnoreCase(SendRequestUtil.PUT))
                        {
                        	
                        }
                        if(res != null)
                        {
                        		notifySuccessComm(res,bean);
                        	
                        }else
                        {
                        	notifyErrorComm(res,bean);
                        }
                    }
                } catch (Exception e)
                {
                    Log.e(TAG, "exceReq Exception " + e.getMessage());
                    notifyErrorComm(e,bean);
                } finally
                {
                    allowNextReq();
                }
            }
        }).start();
    }
    
    // 允许开启新的线程下载队列后的成员
    private void allowNextReq()
    {
    	runningQueue.remove(0);
    	runCount = runCount - 1;
        if (queue.isEmpty())
        {
        	runCount = 1;
        }
        exceBool = true;
    }

    public synchronized void startNet()
    {
        isRun = true;
        exceBool = true;
    }

    public void stopNet()
    {
        isRun = false;
        exceBool = false;
        loadHelper = null;
        if (queue != null && !queue.isEmpty())
            queue.clear();
        synchronized (syncToken)
        {
            syncToken.notify();
        }
        
    }

    public boolean getIsRun()
    {
        return isRun;
    }

    public boolean getExceBool()
    {
        return exceBool;
    }



    private void notifySuccessComm(final HttpRes entity,final ReqBean bean)
    {
    	Handler handler = bean.getHandler();
    	final OnLoadListener listener = bean.getLister();
        if (entity != null)
        {
            if (handler != null)
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (listener != null)
                        {
                            listener.onSuccess(entity,bean);
                        }
                    }
                });
            }
            else
            {
                if (listener != null)
                {
                	  new Thread(new Runnable() {
      					
      					@Override
      					public void run() {
      						listener.onSuccess(entity,bean);
      					}
      				}).start();
                }
            }
        }
        else
        {
            notifyErrorComm(null,bean);
        }
    }

    public void notifyErrorComm(final Object o,final ReqBean bean)
    {
    	Handler handler = bean.getHandler();
    	final OnLoadListener listener = bean.getLister();
        if (handler != null)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (listener != null)
                    {
                    	listener.onError(o,bean);
                    }
                }
            });
        }
        else
        {
        	
            if (listener != null)
            {
                new Thread(new Runnable() {
					
					@Override
					public void run() {
					    listener.onError(o,bean);
					}
				}).start();
            }
        }
    }
}