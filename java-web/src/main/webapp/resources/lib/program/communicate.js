class Communicates {
	constructor() {}
	ctx = $.ctx;

	ajax(options) {
		return new Promise((resolve, reject) => {
            let responsed = false;
            let callbackSuccess  = options.success;
			let callbackError    = options.error;
			let callbackComplete = options.complete;
			if(typeof(callbackSuccess) != 'function') {
				callbackSuccess = function() {};
			}
			if(typeof(callbackError) != 'function') {
				callbackError = function() {};
			}
			if(typeof(callbackComplete) != 'function') {
				callbackComplete = function() {};
			}

			options.success = function(data, textStatus, jqXHR) {
                responsed = true;
                
				try { callbackSuccess(data, textStatus, jqXHR); } catch(errors) {
					reject(String(errors));
					return;
				}

				resolve(data);
			};
            options.error = function(jqXHR, textStatus, errorThrown) {
				responsed = true;
				try { callbackError(jqXHR, textStatus, errorThrown); } catch(errors) { console.log(errors); }
				reject(errorThrown);
			};

			options.complete = function(jqXHR, textStatus) {
				try { callbackComplete(jqXHR, textStatus); } catch(errors) { console.log(errors); }
				if(responsed) return;
				reject('Server communication failed.');
			}

			$.col.ajax(options);
		});
	}

	async checkLogined(jwts) {
		const selfs = this;
        if(! $.col.isEmpty(jwts)) {
            const params = {};
            params.svName = 'login';
            params.svSub  = 'check';
            params.jwt    = jwts;

			const responses = await this.ajax({
				url : selfs.ctx + '/web/json',
                data : params,
                type : 'POST',
                dataType : 'json'
			});
    
			if(responses.success) {
                if(responses.result) {
                    return true;
                } else {
                    return false;
                }
            } else {
                throw (responses.message);
            }
        }
		return false;
	}

	async login(id, pw) {
		const selfs = this;
        const params = {};
        params.svName = 'login';
        params.svSub  = 'login';
		
        const loginPacket = {};
		loginPacket.id = id;
		loginPacket.pw = pw;

		params.login = new HexEncoder().encode(JSON.stringify(loginPacket));

		const responses = await this.ajax({
			url : selfs.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json'
		});

		if(responses.success) {
			return responses.token;
		} else {
			throw responses.message;
		}
	}

	async join(loginPacket) {
		const selfs = this;
        const params = {};
        params.svName = 'login';
        params.svSub  = 'join';
        params.login = new HexEncoder().encode(JSON.stringify(loginPacket));

		const responses = await this.ajax({
			url : selfs.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json'
		});
        if(responses.success) {
			return true;
		}
		throw responses.message;
	}

    async loadColonyList(jwts) {
        const selfs = this;
        const params = {};
        params.svName = 'colony';
        params.svSub  = 'list';
        params.jwt    = jwts;

        const responses = await this.ajax({
            url : selfs.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json'
        });
        if(responses.success) {
            return responses.list;
        }
        throw responses.message;
    }

    async getColony(key, jwts) {
        const selfs = this;
        const params = {};
        params.svName = 'colony';
        params.svSub  = 'detail';
        params.key    = String(key);
        params.jwt    = jwts;

        const responses = await this.ajax({
            url : selfs.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json'
        });
        if(responses.success) {
            return responses.detail;
        }
        throw responses.message;
    }
}
const communicates = new Communicates();