# univariate lstm example
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from numpy import array
from keras.models import Sequential
from keras.layers import BatchNormalization,LSTM, Conv3D,MaxPooling1D, MaxPooling3D,Conv2D,MaxPooling2D, AveragePooling1D,AveragePooling2D, Masking, Flatten
from keras.layers import Dense, ConvLSTM2D,Conv1D,GRU, TimeDistributed, Dense,Dropout,LeakyReLU
from sklearn.metrics import mean_squared_error
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_absolute_error
from tensorflow.keras.callbacks import EarlyStopping
import statistics
import math

# Load the data
data=np.load('/home/alex/Desktop/data/ABILENE/X.npy')
#data=np.load('/home/alex/Desktop/data/GEANT/X.npy')

# Parameters of our time series in the dataclass
SPLIT_TIME = int(len(data) * 0.8)
WINDOW_IN = 10
WINDOW_OUT = 1
NODES=data.shape[2]

# Normalize the data
data = np.clip(data, 0.0, np.percentile(data.flatten(), 99.99998))#abilene
#data = np.clip(data, 0.0, np.percentile(data.flatten(), 99.998))#GEANT
data_split = data[:SPLIT_TIME]
max_list = np.max(data)
min_list = np.min(data)
data = (data - min_list) / (max_list - min_list)
data[np.isnan(data)] = 0  # fill the abnormal data with 0
data[np.isinf(data)] = 0

# define input sequence
# split a multivariate sequence into x=[samples, window, features] y=[sum of features after every window]
def split_sequences(sequences, WINDOW_IN, WINDOW_OUT):
	X, y = list(), list()
	for i in range(len(sequences)):
		# find the end of this pattern
		end_ix = i + WINDOW_IN
		out_end_ix = end_ix + WINDOW_OUT
		# check if we are beyond the dataset
		if out_end_ix > len(sequences):
			break
		# gather input and output parts of the pattern
		seq_x, seq_y = sequences[i:end_ix, :, :], sequences[end_ix:out_end_ix, :, :]
		X.append(seq_x)
		y.append(seq_y)
	return array(X), array(y)
# convert into input/output all the samples
X, y = split_sequences(data, WINDOW_IN, WINDOW_OUT)

# reshape from [samples, timesteps, features] into [samples, timesteps, rows, columns, features]
X = X.reshape(X.shape[0], WINDOW_IN,NODES*NODES)
y = y.reshape(y.shape[0], WINDOW_OUT,NODES*NODES)

# Split into training data and test data
series_train_x = X[:SPLIT_TIME] #[training samples, n_seq, n_steps, features]
series_train_y = y[:SPLIT_TIME] #[sum of training features]
series_test_x = X[SPLIT_TIME:] 
series_test_y = y[SPLIT_TIME:] 

# Define model
model = Sequential()
model.add(GRU(512, return_sequences = True))
model.add(Dropout(0.2))
model.add(MaxPooling1D(pool_size=2))
model.add(GRU(256, return_sequences = True))
model.add(Dropout(0.2))
model.add(MaxPooling1D(pool_size=5))
model.add(GRU(144, return_sequences = True))

model.compile(loss='mae', optimizer='adam', metrics=["accuracy"])  

early_stopping = EarlyStopping(monitor='loss',patience = 10)

# fit model (data = batch_size*samples per epoch)
model.fit(series_train_x, series_train_y, epochs=20, batch_size=128, verbose=1, callbacks=[early_stopping])
model.summary()

# Prediction on the test series
val_forecast = model.predict(series_test_x, verbose=0)

# Rescale to original values
series_test_y=series_test_y*(max_list - min_list) + min_list
val_forecast=val_forecast*(max_list - min_list) + min_list

# calculate RMSE, NMAE, TRE, SRE
series_test_y=series_test_y.reshape(series_test_y.shape[0],series_test_y.shape[1]*series_test_y.shape[2])
val_forecast=val_forecast.reshape(val_forecast.shape[0],val_forecast.shape[1]*val_forecast.shape[2])
testScore1=list()
testScore2=list()
testScore3=list()
testScore4=list()

for t in range(series_test_y.shape[0]):
	testScore1.append(np.sqrt(mean_squared_error(series_test_y[t,:], val_forecast[t,:]))/1000)
	testScore2.append(sum(np.absolute(np.subtract(series_test_y[t,:],val_forecast[t,:])))/sum(np.absolute(series_test_y[t,:])))
	testScore3.append(np.sqrt(sum((np.subtract(series_test_y[t,:],val_forecast[t,:]))**2))/np.sqrt(sum(series_test_y[t,:]**2)))

for i in range(series_test_y.shape[1]):
	if(np.sqrt(sum(series_test_y[:,i]**2))!=0):
		result = np.sqrt(sum((np.subtract(series_test_y[:,i],val_forecast[:,i]))**2))/np.sqrt(sum(series_test_y[:,i]**2))
		testScore4.append(result)
	elif(np.sqrt(sum((np.subtract(series_test_y[:,i],val_forecast[:,i]))**2)))==0:
		result = 0
		testScore4.append(result)

# Printing the scores
print('Mean RMSE Test Score: %.4f Mbps' % (np.mean(testScore1)))
print('Median RMSE Test Score: %.4f Mbps' % (statistics.median(testScore1)))
print('Std RMSE Test Score: %.4f Mbps' % (np.std(testScore1)))
print('Max RMSE Test Score: %.4f Mbps' % (max(testScore1)))

print('Mean NMAE Test Score: %.4f' % (np.mean(testScore2)))
print('Median NMAE Test Score: %.4f' % (statistics.median(testScore2)))
print('Std NMAE Test Score: %.4f' % (np.std(testScore2)))
print('Max NMAE Test Score: %.4f' % (max(testScore2)))

print('Mean TRE Test Score: %.4f' % (np.mean(testScore3)))
print('Median TRE Test Score: %.4f' % (statistics.median(testScore3)))
print('Std TRE Test Score: %.4f' % (np.std(testScore3)))
print('Max TRE Test Score: %.4f' % (max(testScore3)))

print('Mean SRE Test Score: %.4f' % (np.mean(testScore4)))
print('Median SRE Test Score: %.4f' % (statistics.median(testScore4)))
print('Std SRE Test Score: %.4f' % (np.std(testScore4)))
print('Max SRE Test Score: %.4f' % (max(testScore4)))

# Plot
plt.figure(figsize=(10, 6))
plt.plot(np.squeeze(series_test_y[:,20]), label="validation set")
plt.plot(np.squeeze(val_forecast[:,20]), label="predicted")
plt.xlabel("Timestep")
plt.ylabel("Value")
plt.legend()
plt.show()
