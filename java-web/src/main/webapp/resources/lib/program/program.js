
/** 메인 컴포넌트 */
class Colonization extends React.Component {
    ctx = '/';
    state = {
        jwtToken : null,
        screen : 'login',
        colonies : [],
        colony : null
    };

    constructor(props) {
        super(props);
        this.ctx = props.ctx;
    }
    componentDidMount() {
        const selfs = this;
        this.checkLogined().then((logined) => {
            if(logined) {
                selfs.loadColonies().then(() => {
                    selfs.setState({screen: 'main'});
                }).catch((errMsg) => {
                    alert(errMsg);
                    selfs.setState({screen: 'login'});
                });
            } else {
                selfs.setState({screen: 'login'});
            }
        }).catch((errMsg) => {
            alert(errMsg);
        });
    }

    getJWT() {
        const jwts = sessionStorage.getItem('col_jwtToken');
        if($.col.isEmpty(jwts)) return null;
        return jwts;
    }

    setJWT(token, callback) {
        if(! $.col.isEmpty(token)) {
            if(typeof(token) == 'object') token = JSON.stringify(token);
            sessionStorage.setItem('col_jwtToken', token);
        } else {
            sessionStorage.removeItem('col_jwtToken');
        }
        
        this.setState({jwtToken: token}, () => {
            if(typeof(callback) == 'function') callback();
        });
    }

    checkLogined() {
        const selfs = this;
        return new Promise((resolve, reject) => {
            const jwts = selfs.getJWT();
            if(! $.col.isEmpty(jwts)) {
                const params = {};
                params.svName = 'login';
                params.svSub  = 'check';
                params.jwt    = jwts;
    
                let responsed = false;
                const ajaxOptions = {
                    url : selfs.ctx + '/web/json',
                    data : params,
                    type : 'POST',
                    dataType : 'json',
                    success : function(responses) {
                        responsed = true;
                        if(responses.success) {
                            if(responses.result) {
                                selfs.setState({jwtToken: jwts}, () => {  resolve(true); }); 
                            } else {
                                selfs.setJWT(null, () => { resolve(false); });
                            }
                        } else {
                            reject(responses.message);
                        }
                    }, complete : function() {
                        if(! responsed) {
                            reject('오류 : 서버와 통신에 실패하였습니다.');
                        }
                    }
                };
                $.col.ajax(ajaxOptions);
            } else {
                resolve(false);
            }
        });
    }

    loadColonies() {
        const selfs = this;
        return new Promise((resolve, reject) => {
            const jwts = selfs.getJWT();
            const params = {};
            params.svName = 'colony';
            params.svSub  = 'list';
            params.jwt    = jwts;

            let responsed = false;
            const ajaxOptions = {
                url : selfs.ctx + '/web/json',
                data : params,
                type : 'POST',
                dataType : 'json',
                success : function(responses) {
                    responsed = true;
                    if(responses.success) {
                        selfs.setState({ colonies : responses.list }, () => { 
                            let exists = false;
                            if(selfs.state.colony != null) {
                                for(const colInfoOne of responses.list) {
                                    if(colInfoOne.key == selfs.state.colony.key) {
                                        exists = true; break;
                                    }
                                }

                                if(exists) {
                                    resolve(responses.list);
                                } else {
                                    selfs.selectColony(responses.list[0].key).then(() => {
                                        resolve(responses.list);
                                    });
                                }
                            } else {
                                selfs.selectColony(responses.list[0].key).then(() => {
                                    resolve(responses.list);
                                });
                            }
                        });
                    } else {
                        reject(responses.message);
                    }
                }, complete : function() {
                    if(! responsed) {
                        reject('오류 : 서버와 통신에 실패하였습니다.');
                    }
                }
            };
            $.col.ajax(ajaxOptions);
        });
    }

    selectColony(key) {
        const selfs = this;
        return new Promise((resolve, reject) => {
            const jwts = selfs.getJWT();
            const params = {};
            params.svName = 'colony';
            params.svSub  = 'detail';
            params.key    = String(key);
            params.jwt    = jwts;

            let responsed = false;
            const ajaxOptions = {
                url : selfs.ctx + '/web/json',
                data : params,
                type : 'POST',
                dataType : 'json',
                success : function(responses) {
                    responsed = true;
                    if(responses.success) {
                        selfs.setState({colony : responses.detail}, () => { resolve(responses.detail); });
                    } else {
                        reject(responses.message);
                    }
                }, complete : function() {
                    if(! responsed) {
                        reject('오류 : 서버와 통신에 실패하였습니다.');
                    }
                }
            };
            $.col.ajax(ajaxOptions);
        });
    }

    render() {
        if($.col.isEmpty(this.state.screen)) {
            this.state.screen = 'login';
        }
        if(this.state.screen == 'login') return (<LoginScreen superInstance={this}/>);
        if(this.state.screen == 'join' ) return (<JoinScreen  superInstance={this}/>);
        if(this.state.screen == 'main' ) return (<MainScreen  superInstance={this} />);
        return (<LoginScreen superInstance={this}/>);
    }
}

class ColCommonComponent extends React.Component {
    ctx = '/';
    superInstance = null;
    constructor(props) {
        super(props);
        this.superInstance = props.superInstance;
        this.ctx = this.superInstance.ctx;
    }
    getRoot() {
        return $('#div_colonization_root');
    }
    goto(scrName) {
        this.superInstance.setState({screen: scrName});
    }
}

class LoginScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }

    onLoginRequested() {
        const selfs  = this;
        const params = {};
        params.svName = 'login';
        params.svSub  = 'login';

        const loginPacket = {};

        loginPacket.id =  this.getRoot().find('.inp_loginscr_id').val();
        loginPacket.pw =  this.getRoot().find('.inp_loginscr_pw').val();

        params.login = new HexEncoder().encode(JSON.stringify(loginPacket));

        let responsed = false;
        const ajaxOptions = {
            url : this.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json',
            success : function(responses) {
                responsed = true;
                if(responses.success) {
                    try {
                        const token = responses.token;
                        selfs.superInstance.setJWT(token, () => { selfs.goto('main'); });
                    } catch(e) {
                        alert('오류 : ' + e.message);
                    }
                } else {
                    alert('오류 : ' + responses.message);
                }
            }, complete : function() {
                if(!responsed) {
                    alert('오류 : 서버와 통신에 실패하였습니다.');
                }
            }
        };
        $.col.ajax(ajaxOptions);
    }

    onJoinRequested() {
        this.goto('join');
    }

    render() {
        return (
            <div>
                <form onSubmit={() => {return false;}} className="form form_loginscr form_loginscr_main">
                    <table className='layout full table table_view table_loginscr table_loginscr_main'>
                        <colgroup>
                            <col style={{width: '135px'}}/>
                            <col/>
                        </colgroup>
                        <tbody>
                            <tr className="tr tr_loginscr tr_loginctr_pw">
                                <th>ID</th>
                                <td><input type='text' name='id' className='full inp inp_tx inp_id inp_loginscr_id'/></td>
                            </tr>
                            <tr className="tr tr_loginscr tr_loginctr_pw">
                                <th>Password</th>
                                <td><input type='password' name='password' className='full inp inp_pw inp_loginscr_pw'/></td>
                            </tr>
                            <tr className="tr tr_loginscr tr_loginctr_ctl">
                                <td colspan='2'>
                                    <button type='button' className='btn btn_loginscr btn_loginscr_login' onClick={() => { this.onLoginRequested(); }}>로그인</button>
                                    <button type='button' className='btn btn_loginscr btn_loginscr_join'  onClick={() => { this.onJoinRequested();  }}>가입</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        );
    }
}

class JoinScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }

    onJoinRequested() {
        const selfs  = this;
        const params = {};
        params.svName = 'login';
        params.svSub  = 'join';

        const loginPacket = {};

        loginPacket.id     =  this.getRoot().find('.inp_joinscr_id').val();
        loginPacket.pw     =  this.getRoot().find('.inp_joinscr_pw').val();
        loginPacket.pwc    =  this.getRoot().find('.inp_joinscr_pw_check').val();

        if(loginPacket.pw != loginPacket.pwc) {
            alert('비밀번호 값과 확인 값이 일치하지 않습니다.');
            return;
        }

        loginPacket.name =  this.getRoot().find('.inp_joinscr_name').val();
        params.login = new HexEncoder().encode(JSON.stringify(loginPacket));

        let responsed = false;
        const ajaxOptions = {
            url : this.ctx + '/web/json',
            data : params,
            type : 'POST',
            dataType : 'json',
            success : function(responses) {
                responsed = true;
                if(responses.success) {
                    try {
                        selfs.goto('login');
                    } catch(e) {
                        alert('오류 : ' + e.message);
                    }
                } else {
                    alert('오류 : ' + responses.message);
                }
            }, complete : function() {
                if(!responsed) {
                    alert('오류 : 서버와 통신에 실패하였습니다.');
                }
            }
        };
        $.col.ajax(ajaxOptions);
    }

    render() {
        return (
            <div>
                <form onSubmit={() => {return false;}} className="form form_joinscr form_joinscr_main">
                    <table className='layout full table table_view table_joinscr table_joinscr_main'>
                        <colgroup>
                            <col style={{width: '135px'}}/>
                            <col/>
                        </colgroup>
                        <tbody>
                            <tr className="tr tr_joinscr tr_joinctr_id">
                                <th>ID</th>
                                <td><input type='text' name='id' className='full inp inp_tx inp_id inp_joinscr_id'/></td>
                            </tr>
                            <tr className="tr tr_joinscr tr_joinctr_pw">
                                <th>Password</th>
                                <td><input type='password' name='password' className='full inp inp_pw inp_joinscr_pw'/></td>
                            </tr>
                            <tr className="tr tr_joinscr tr_joinctr_pw_check">
                                <th>Password 확인</th>
                                <td><input type='password' name='password_check' className='full inp inp_pw inp_joinscr_pw_check'/></td>
                            </tr>
                            <tr className="tr tr_joinscr tr_joinctr_name">
                                <th>E-Mail</th>
                                <td><input type='text' name='name' className='full inp inp_tx inp_name inp_joinscr_name'/></td>
                            </tr>
                            <tr className="tr tr_joinscr tr_joinctr_ctl">
                                <td colspan='2'>
                                    <button type='button' className='btn btn_joinscr btn_joinscr_join'   onClick={() => { this.onJoinRequested(); }}>가입</button>
                                    <button type='button' className='btn btn_joinscr btn_joinscr_cancel' onClick={() => { this.goto('login'); }}>취소</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        );
    }
}

class MainScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <div className="div div_colonization_main">
                <TopToolbar superInstance={this.superInstance} />
                <div className="div div_colonization_body">
                    {
                        (this.superInstance.state.colony != null)
                        ? <ColonyScreen superInstance={this.superInstance} colony={this.superInstance.state.colony}/>
                        : <div className="div div_colonization_nocolony">식민지를 선택해 주십시오.</div>
                    }
                </div>
            </div>
        );
    }
}

class TopToolbar extends ColCommonComponent {
    constructor(props) {
        super(props);
    }

    onSelectColony(e) {
        const key = e.target.value;
        this.superInstance.selectColony(key).then(() => {
            // 선택 완료
        }).catch((errMsg) => {
            alert(errMsg);
        });
    }

    render() {
        return (
            <div className="div toolbar div_colonization_toptoolbar">
                <select className="select select_toptoolbar select_toptoolbar_colonies" onChange={(e) => { this.onSelectColony(e); }}>
                    { this.superInstance.state.colonies.map((colInfo) => {
                        return (<option key={colInfo.key} value={colInfo.key} selected={(this.superInstance.state.colony != null) && (this.superInstance.state.colony.key == colInfo.key)}>{colInfo.name}</option>);
                    })}
                </select>
            </div>
        );
    }
}

class ColonyScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }
    render() {
        return (
            <div className="div div_colonization_colony">
                
            </div>
        );
    }
}