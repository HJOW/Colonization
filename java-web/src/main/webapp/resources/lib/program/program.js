
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
                    selfs.setPState({screen: 'main'});
                }).catch((errMsg) => {
                    alert(errMsg);
                    selfs.setPState({screen: 'login'});
                });
            } else {
                selfs.setPState({screen: 'login'});
            }
        }).catch((errMsg) => {
            alert(errMsg);
        });
    }

    setPState(stateJson) {
        const selfs = this;
        return new Promise((resolve, reject) => {
            selfs.setState(stateJson, () => {
                resolve(true);
            });
        });
    }

    getJWT() {
        const jwts = sessionStorage.getItem('col_jwtToken');
        if($.col.isEmpty(jwts)) return null;
        return jwts;
    }

    setJWT(token, callback) {
        const selfs = this;
        return new Promise((resolve, reject) => {
            if(! $.col.isEmpty(token)) {
                if(typeof(token) == 'object') token = JSON.stringify(token);
                sessionStorage.setItem('col_jwtToken', token);
            } else {
                token = null;
                sessionStorage.removeItem('col_jwtToken');
            }

            selfs.setState({jwtToken: token}, () => {
                if(typeof(callback) == 'function') callback();
                resolve(token);
            });
        });

    }

    async checkLogined() {
        const jwts  = this.getJWT();
        let res = null;

        if($.col.isEmpty(jwts)) return false;

        res = await communicates.checkLogined(jwts);
        if(res == null) {
            await this.setJWT(null);
            return false;
        } else {
            await this.setPState({jwtToken: jwts});
            return true;
        }
    }

    async loadColonies() {
        const jwts = this.getJWT();
        if($.col.isEmpty(jwts)) {
            resolve([]);
            return;
        }

        let lists = null;
        lists = await communicates.loadColonyList(jwts);

        await this.setPState({ colonies : lists });
        let exists = false;
        if(this.state.colony != null) {
            for(const colInfoOne of lists) {
                if(colInfoOne.key == this.state.colony.key) {
                    exists = true; break;
                }
            }

            if(exists) {
                // DO nothing
            } else {
                if(lists.length <= 0) return [];
                await this.selectColony(lists[0].key);
            }
        } else {
            if(lists.length <= 0) return [];
            await this.selectColony(lists[0].key);
        }
        return lists;
    }

    async selectColony(key) {
        const jwts = this.getJWT();

        let details = await communicates.getColony(key, jwts);
        await this.setPState({colony : details});
        return details;
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
    setPState(stateJson) {
        const selfs = this;
        return new Promise((resolve, reject) => {
            selfs.setState(stateJson, () => {
                resolve(true);
            });
        });
    }
    getRoot() {
        return $('#div_colonization_root');
    }
    async goto(scrName) {
        await this.superInstance.setPState({screen: scrName});
    }
}

class LoginScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }

    async onLoginRequested() {
        const id = this.getRoot().find('.inp_loginscr_id').val();
        const pw = this.getRoot().find('.inp_loginscr_pw').val();

        let token = null;
        token = await communicates.login(id, pw);
        await this.superInstance.setJWT(token);
        await this.superInstance.loadColonies();
        await this.goto('main');
    }

    async onJoinRequested() {
        await this.goto('join');
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

    async onJoinRequested() {
        const loginPacket = {};

        loginPacket.id     =  this.getRoot().find('.inp_joinscr_id').val();
        loginPacket.pw     =  this.getRoot().find('.inp_joinscr_pw').val();
        loginPacket.pwc    =  this.getRoot().find('.inp_joinscr_pw_check').val();

        if(loginPacket.pw != loginPacket.pwc) {
            alert('비밀번호 값과 확인 값이 일치하지 않습니다.');
            return;
        }

        loginPacket.name =  this.getRoot().find('.inp_joinscr_name').val();
        let responses = null;

        responses = await communicates.join(loginPacket);
        if(responses) {
            await this.goto('login');
        }
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

    componentDidMount() {

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
    state = {
        city : null
    };
    constructor(props) {
        super(props);
        if(this.props.colony.cities.length >= 1) this.state.city = this.props.colony.cities[0];
    }

    onClickCitySelect(cityKey) {
        const colony = this.props.colony;
        for(let idx=0; idx<colony.cities.length; idx++) {
            const cityOne = colony.cities[idx];
            if(cityKey == String(cityOne.key)) {
                this.setState({city: cityOne});
                return;
            }
        }
    }

    render() {
        const selfs  = this;
        const colony = this.props.colony;
        return (
            <div className="div div_element div_colonization_colony" data-key={colony.key}>
                <table className="table layout full">
                    <tbody>
                        <tr>
                            <td>
                                <input type="text" className="inp inp_colonyscr inp_colonyscr_name" defaultValue={colony.name} readOnly={true}/>
                                <progress className="prog prog_colonyscr prog_colonyscr_hp" max={colony.maxHp} value={colony.hp}></progress>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <textarea className="full ta ta_colonyscr ta_colonyscr_desc" readOnly={true}>


                                </textarea>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div className="div_colonyscr div_colonyscr_cities_tabs">
                                    {
                                        colony.cities.map((city) => {
                                            let classes = 'btn btn_colonyscr btn_colonyscr_sel_city';
                                            if(this.state.city != null) {
                                                if(this.state.city.key == city.key) classes = classes + ' selected';
                                            }
                                            return (<button type='button' onClick={() => { selfs.onClickCitySelect(String(city.key)) }} className={classes} data-key={city.key}>{city.name}</button> );
                                        })
                                    }
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div className="div_colonyscr div_colonyscr_cities_conent">
                                    {
                                        this.state.city == null ? (<div></div>) : (
                                            <CityScreen superInstance={this.superInstance} colony={colony} city={this.state.city}/>
                                        )
                                    }
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}

class CityScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }
    render() {
        const city = this.props.city;
        console.log(city);
        return (
            <div className="div div_element div_cityscr div_cityscr_main div_colonization_city" data-key={city.key}>
                <table className="table layout full">
                    <tbody>
                        <tr>
                            <td colSpan={2}>
                                <input type="text" className="inp inp_cityscr inp_cityscr_name" defaultValue={city.name} readOnly={true}/>
                                <progress className="prog prog_cityscr prog_cityscr_hp" max={city.maxHp} value={city.hp}></progress>
                            </td>
                        </tr>
                        <tr>
                            <td>

                            </td>
                            <td>
                                {
                                    city.facilities.map((facility) => {
                                        return (
                                            <FacilityScreen superInstance={this.superInstance} colony={this.props.colony} city={city} facility={facility}/>
                                        );
                                    });
                                }
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        );
    }
}

class FacilityScreen extends ColCommonComponent {
    constructor(props) {
        super(props);
    }
    render() {
        const fac = this.props.facility;
        return (
            <div className="div div_element div_facscr div_facscr_main div_colonization_facility" data-key={fac.key}>

            </div>
        );
    }
}