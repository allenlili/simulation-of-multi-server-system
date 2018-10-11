% Week 8, Q1, Part (a): Transient removal   

% load the traces 
for i = 1:20
   S = strcat('trace', num2str(i));
   load(S);
end 

% put the traces in an array
nsim = 20;     % number of simulation
ndp = 30000;  % number of data points in each simulation
T = zeros(nsim,ndp);
for i = 1:20
    eval(['T(i,:) = trace',num2str(i),';']);
end    

w = 8000;
% Drop the first one thousand points as the transient
% Compute the mean from data points 1001 to 20,000
mt = mean(T(:,w+1:end)');

% Find the mean and standard deviation of mt
mean(mt)
std(mt)








